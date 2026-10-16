import {
  computed,
  createApp,
  defineComponent,
  h,
  onBeforeMount,
  onBeforeUnmount,
  onBeforeUpdate,
  onMounted,
  onUnmounted,
  onUpdated,
  ref,
  watch,
} from 'vue';

// Vue 3.5.43 的普通同步客户端案例。日志数组/输出函数应保持非响应式。
export function createMainlineApp(record = console.log) {
  let count;

  function increment() {
    if (!count) throw new Error('请先调用 app.mount()');
    count.value++;
  }

  function readCount() {
    return count?.value;
  }

  function registerHooks(name) {
    onBeforeMount(() => record(`${name}:beforeMount`));
    onMounted(() => record(`${name}:mounted`));
    onBeforeUpdate(() => record(`${name}:beforeUpdate`));
    onUpdated(() => record(`${name}:updated`));
    onBeforeUnmount(() => record(`${name}:beforeUnmount`));
    onUnmounted(() => record(`${name}:unmounted`));
  }

  const CounterValue = defineComponent({
    name: 'CounterValue',
    props: { value: { type: Number, required: true } },
    setup(props) {
      record('child:setup');
      registerHooks('child');
      return () => {
        record('child:render');
        return h('p', { 'data-child': '' }, `两倍：${props.value}`);
      };
    },
  });

  const Root = defineComponent({
    name: 'MainlineRoot',
    setup(_props, { expose }) {
      record('root:setup');
      count = ref(0);
      const doubled = computed(() => {
        record('computed:evaluate');
        return count.value * 2;
      });
      watch(count, (value, previous) => record(`watch:${previous}->${value}`), { flush: 'pre' });
      registerHooks('root');
      expose({ increment, readCount });
      return () => {
        record('root:render');
        return h('section', { 'data-mainline-root': '' }, [
          h('button', { type: 'button', 'data-count': '', onClick: increment }, `计数：${count.value}`),
          h(CounterValue, { value: doubled.value }),
        ]);
      };
    },
  });

  const app = createApp(Root);
  record('app:created');
  return { app, increment, readCount };
}
