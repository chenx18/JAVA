// Educational subset: plain objects, effects, computed, a job queue and DOM VNodes.
const dependencies = new WeakMap();
const proxies = new WeakMap();
const rawTargets = new WeakMap();
const ITERATE = Symbol('iterate');
let activeEffect = null;
let activeScope = null;

function clean(runner) {
  for (const dep of runner.deps) dep.delete(runner);
  runner.deps.clear();
}

export function effect(fn, options = {}) {
  if (typeof fn !== 'function') throw new TypeError('effect requires a function');
  function runner() {
    if (!runner.active) return fn();
    if (runner.running) return;
    clean(runner);
    const previous = activeEffect;
    activeEffect = runner;
    runner.running = true;
    try { return fn(); }
    finally { activeEffect = previous; runner.running = false; }
  }
  runner.active = true;
  runner.running = false;
  runner.deps = new Set();
  runner.scheduler = options.scheduler;
  runner.computed = Boolean(options.computed);
  runner.stop = () => { clean(runner); runner.active = false; };
  activeScope?.add(runner);
  if (!options.lazy) {
    try { runner(); } catch (error) { runner.stop(); throw error; }
  }
  return runner;
}

function track(target, key) {
  if (!activeEffect?.active) return;
  let byKey = dependencies.get(target);
  if (!byKey) dependencies.set(target, byKey = new Map());
  let dep = byKey.get(key);
  if (!dep) byKey.set(key, dep = new Set());
  dep.add(activeEffect);
  activeEffect.deps.add(dep);
}

function trigger(target, key, structural = false) {
  const byKey = dependencies.get(target);
  if (!byKey) return;
  const pending = new Set(byKey.get(key));
  if (structural) for (const runner of byKey.get(ITERATE) ?? []) pending.add(runner);
  // Invalidate derived values before notifying ordinary consumers.
  const ordered = [...pending].sort((a, b) => Number(b.computed) - Number(a.computed));
  for (const runner of ordered) {
    if (!runner.active || runner === activeEffect) continue;
    if (runner.scheduler) runner.scheduler(runner);
    else runner();
  }
}

export function reactive(target) {
  if (rawTargets.has(target)) return target;
  if (target === null || typeof target !== 'object') throw new TypeError('plain object required');
  const prototype = Object.getPrototypeOf(target);
  if (prototype !== Object.prototype && prototype !== null) throw new TypeError('plain object required');
  if (!Object.isExtensible(target)) throw new TypeError('extensible object required');
  if (proxies.has(target)) return proxies.get(target);
  const proxy = new Proxy(target, {
    get(raw, key, receiver) {
      const value = Reflect.get(raw, key, receiver);
      track(raw, key);
      return value !== null && typeof value === 'object' ? reactive(value) : value;
    },
    set(raw, key, value, receiver) {
      const existed = Object.hasOwn(raw, key);
      const before = Reflect.get(raw, key, receiver);
      const unwrapped = rawTargets.get(value) ?? value;
      const ok = Reflect.set(raw, key, unwrapped, receiver);
      if (ok && (!existed || !Object.is(before, unwrapped))) trigger(raw, key, !existed);
      return ok;
    },
    deleteProperty(raw, key) {
      const existed = Object.hasOwn(raw, key);
      const ok = Reflect.deleteProperty(raw, key);
      if (ok && existed) trigger(raw, key, true);
      return ok;
    },
    has(raw, key) { track(raw, key); return Reflect.has(raw, key); },
    ownKeys(raw) { track(raw, ITERATE); return Reflect.ownKeys(raw); }
  });
  proxies.set(target, proxy);
  rawTargets.set(proxy, target);
  return proxy;
}

export function ref(value) { return reactive({ value }); }

export function computed(getter) {
  let dirty = true;
  let cached;
  const result = {};
  const runner = effect(getter, {
    lazy: true, computed: true,
    scheduler() {
      if (!dirty) { dirty = true; trigger(result, 'value'); }
    }
  });
  Object.defineProperty(result, 'value', {
    get() {
      track(result, 'value');
      if (dirty) { cached = runner(); dirty = false; }
      return cached;
    }
  });
  return result;
}

const jobs = new Set();
const resolved = Promise.resolve();
let currentFlush = null;

export function queueJob(job) {
  jobs.add(job);
  if (!currentFlush) {
    currentFlush = resolved.then(() => {
      const counts = new Map();
      let failure;
      let failed = false;
      try {
        while (jobs.size) {
          const batch = [...jobs];
          jobs.clear();
          for (const task of batch) {
            try {
              const count = (counts.get(task) ?? 0) + 1;
              counts.set(task, count);
              if (count > 100) throw new Error('recursive update limit');
              task();
            } catch (error) {
              if (!failed) { failure = error; failed = true; }
            }
          }
        }
      } finally {
        jobs.clear();
        currentFlush = null;
      }
      if (failed) throw failure;
    });
  }
  return currentFlush;
}

export function nextTick() { return currentFlush ?? resolved; }

const Text = Symbol('text');
function textVNode(value) { return { type: Text, text: String(value), key: null, el: null }; }

export function h(type, props = {}, children = []) {
  if (typeof type !== 'string') throw new TypeError('element tag required');
  const list = (Array.isArray(children) ? children : [children]).map(child => {
    if (typeof child === 'string' || typeof child === 'number') return textVNode(child);
    if (!child || !('type' in child)) throw new TypeError('VNode or text required');
    return child;
  });
  const keys = list.map(child => child.key);
  if (keys.some(key => key != null)) {
    if (keys.some(key => key == null) || new Set(keys).size !== keys.length) {
      throw new Error('siblings must have unique keys, or all omit keys');
    }
  }
  return { type, props: { ...props }, key: props.key ?? null, children: list, el: null };
}

function patchProp(element, key, before, after) {
  if (key === 'key') return;
  if (/^on[A-Z]/.test(key)) {
    const name = key.slice(2).toLowerCase();
    const events = element.__miniEvents ??= new Map();
    const existing = events.get(key);
    if (after == null) {
      if (existing) element.removeEventListener(name, existing);
      events.delete(key);
    } else {
      if (typeof after !== 'function') throw new TypeError('event callback required');
      if (existing) existing.value = after;
      else {
        const listener = event => listener.value(event);
        listener.value = after;
        events.set(key, listener);
        element.addEventListener(name, listener);
      }
    }
  } else if (key === 'value' || key === 'checked' || key === 'disabled') {
    element[key] = key === 'value' ? (after ?? '') : Boolean(after);
  } else if (after == null) element.removeAttribute(key);
  else element.setAttribute(key, String(after));
}

function unmount(vnode) {
  if (vnode.type !== Text) {
    for (const child of vnode.children) unmount(child);
    for (const key of vnode.el.__miniEvents?.keys() ?? []) patchProp(vnode.el, key, null, null);
  }
  vnode.el.remove();
}

function patchChildren(before, after, parent) {
  const keyed = after.some(node => node.key != null) || before.some(node => node.key != null);
  if (!keyed) {
    const count = Math.min(before.length, after.length);
    for (let i = 0; i < count; i++) patch(before[i], after[i], parent);
    for (let i = count; i < before.length; i++) unmount(before[i]);
    for (let i = count; i < after.length; i++) patch(null, after[i], parent);
    return;
  }
  if ([...before, ...after].some(node => node.key == null)) {
    for (const node of before) unmount(node);
    for (const node of after) patch(null, node, parent);
    return;
  }
  const oldByKey = new Map(before.map(node => [node.key, node]));
  const newKeys = new Set(after.map(node => node.key));
  for (const old of before) if (!newKeys.has(old.key)) unmount(old);
  let anchor = null;
  for (let i = after.length - 1; i >= 0; i--) {
    const node = after[i];
    patch(oldByKey.get(node.key) ?? null, node, parent, anchor);
    if (node.el.nextSibling !== anchor) parent.insertBefore(node.el, anchor);
    anchor = node.el;
  }
}

function patch(before, after, parent, anchor = null) {
  if (before && (before.type !== after.type || before.key !== after.key)) {
    anchor = before.el.nextSibling;
    unmount(before);
    before = null;
  }
  if (!before) {
    after.el = after.type === Text
      ? parent.ownerDocument.createTextNode(after.text)
      : parent.ownerDocument.createElement(after.type);
    if (after.type !== Text) {
      for (const [key, value] of Object.entries(after.props)) patchProp(after.el, key, null, value);
      for (const child of after.children) patch(null, child, after.el);
    }
    parent.insertBefore(after.el, anchor);
    return;
  }
  after.el = before.el;
  if (after.type === Text) {
    if (after.text !== before.text) after.el.nodeValue = after.text;
    return;
  }
  for (const [key, value] of Object.entries(after.props)) {
    if (!Object.is(before.props[key], value)) patchProp(after.el, key, before.props[key], value);
  }
  for (const key of Object.keys(before.props)) {
    if (!Object.hasOwn(after.props, key)) patchProp(after.el, key, before.props[key], null);
  }
  patchChildren(before.children, after.children, after.el);
}

export function createApp(setup) {
  const scope = new Set();
  let tree = null, mounted = false, used = false;
  return {
    mount(container) {
      if (used) throw new Error('create a new app to mount again');
      used = true;
      const previousScope = activeScope;
      activeScope = scope;
      try {
        const render = setup();
        if (typeof render !== 'function') throw new TypeError('setup must return render');
        mounted = true;
        let update;
        const job = () => { if (mounted) update(); };
        update = effect(() => {
          const next = render();
          patch(tree, next, container);
          tree = next;
        }, { scheduler: () => queueJob(job) });
      } catch (error) {
        mounted = false;
        for (const runner of scope) runner.stop();
        scope.clear();
        throw error;
      } finally { activeScope = previousScope; }
    },
    unmount() {
      mounted = false;
      for (const runner of scope) runner.stop();
      scope.clear();
      if (tree) { unmount(tree); tree = null; }
    }
  };
}

export function compileText(template) {
  // Text and simple identifier interpolation only; no HTML or expression evaluation.
  const parts = [];
  const pattern = /\{\{\s*([A-Za-z_$][\w$]*)\s*\}\}/g;
  let position = 0, match;
  while ((match = pattern.exec(template))) {
    parts.push({ text: template.slice(position, match.index) }, { key: match[1] });
    position = pattern.lastIndex;
  }
  parts.push({ text: template.slice(position) });
  return state => h('span', {}, parts.map(part => {
    if (Object.hasOwn(part, 'text')) return part.text;
    if (!Object.hasOwn(state, part.key)) throw new Error('unknown binding: ' + part.key);
    return String(state[part.key]);
  }).join(''));
}
