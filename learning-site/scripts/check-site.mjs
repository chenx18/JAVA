import assert from 'node:assert/strict';
import { existsSync, readFileSync, readdirSync, statSync } from 'node:fs';
import path from 'node:path';
import { fileURLToPath } from 'node:url';
import { parse } from 'parse5';

const root = fileURLToPath(new URL('../', import.meta.url));
const output = path.resolve(root, process.env.SITE_OUT_DIR || '.vitepress/dist');
const base = process.env.SITE_BASE || '/';
const origin = 'https://knowledge.example';
assert(existsSync(output), 'Run npm run build before npm run check');

const walk = directory => readdirSync(directory, { withFileTypes: true }).flatMap(entry =>
  entry.isDirectory() ? walk(path.join(directory, entry.name)) : [path.join(directory, entry.name)]);
const files = walk(output).filter(file => file.endsWith('.html'));
const documents = new Map();

for (const file of files) {
  const anchors = new Set();
  const links = [];
  const assets = [];
  let headings = 0;
  function visit(node) {
    const attrs = Object.fromEntries((node.attrs || []).map(attr => [attr.name, attr.value]));
    if (attrs.id) anchors.add(attrs.id);
    if (node.tagName === 'a' && attrs.href !== undefined) links.push(attrs.href);
    if (attrs.src && ['img', 'script', 'source'].includes(node.tagName)) assets.push(attrs.src);
    if (node.tagName === 'link' && ['stylesheet', 'icon', 'modulepreload'].includes(attrs.rel)) assets.push(attrs.href);
    if (node.tagName === 'h1') headings++;
    for (const child of node.childNodes || []) visit(child);
  }
  visit(parse(readFileSync(file, 'utf8')));
  documents.set(file, { anchors, links, assets, headings });
}

const errors = new Set();
let checkedLinks = 0;
let checkedAnchors = 0;
for (const [file, doc] of documents) {
  const relative = path.relative(output, file).replaceAll('\\', '/');
  const currentUrl = origin + base + relative;
  if (relative !== '404.html' && !relative.includes('/examples/')) assert.equal(doc.headings, 1, `${relative}: expected one main heading`);
  for (const href of [...doc.links, ...doc.assets]) {
    if (/^(?:https?:|mailto:|tel:|data:|javascript:|\/\/)/i.test(href)) continue;
    const url = new URL(href, currentUrl);
    const pathname = decodeURIComponent(url.pathname);
    if (!pathname.startsWith(base)) {
      errors.add(`${relative}: link escapes deployment base: ${href}`);
      continue;
    }
    let target = path.resolve(output, pathname.slice(base.length));
    if (target !== output && !target.startsWith(output + path.sep)) {
      errors.add(`${relative}: link escapes output: ${href}`);
      continue;
    }
    if (existsSync(target) && statSync(target).isDirectory()) target = path.join(target, 'index.html');
    if (!existsSync(target) && !path.extname(target)) target += '.html';
    checkedLinks++;
    if (!existsSync(target)) { errors.add(`${relative}: missing ${href}`); continue; }
    if (url.hash && documents.has(target)) {
      const anchor = decodeURIComponent(url.hash.slice(1));
      if (!documents.get(target).anchors.has(anchor)) errors.add(`${relative}: missing anchor ${href}`);
      checkedAnchors++;
    }
  }
}

const catalog = JSON.parse(readFileSync(path.join(root, '.content/catalog.json'), 'utf8'));
const sourceRoot = path.resolve(root, '../docs/AI前端全栈教材');
const sourcePages = walk(sourceRoot).filter(file => file.endsWith('.md') && path.basename(file) !== '技术路线.md');
const sourceTopics = new Set();
for (const source of sourcePages) {
  const relative = path.relative(sourceRoot, source);
  const parts = relative.split(path.sep);
  if (parts.length > 1) sourceTopics.add(parts[0]);
  assert(existsSync(path.join(output, 'course', relative.replace(/\.md$/, '.html'))), `Unpublished chapter: ${relative}`);
}
assert.deepEqual(new Set(catalog.topics.map(topic => topic.directory)), sourceTopics, 'Update topic definitions to cover the source series');
assert(catalog.total.chapters > 0 && catalog.total.questions > 0, 'Content is empty');
assert(existsSync(path.join(output, '.nojekyll')), 'GitHub Pages requires .nojekyll');
assert(existsSync(path.join(output, 'favicon.svg')), 'Missing favicon');
assert.equal(errors.size, 0, [...errors].slice(0, 25).join('\n'));
console.log(`PASS: ${files.length} HTML pages, ${checkedLinks} local links/assets, ${checkedAnchors} anchors; base=${base}`);
console.log(`Content: ${catalog.topics.length} topics, ${catalog.total.chapters} chapters, ${catalog.total.questions} questions`);
