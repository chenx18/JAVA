import { existsSync, mkdirSync, readFileSync, readdirSync, unlinkSync, writeFileSync } from 'node:fs';
import path from 'node:path';
import { fileURLToPath } from 'node:url';

export const siteRoot = fileURLToPath(new URL('../', import.meta.url));
export const sourceRoot = path.resolve(siteRoot, '../docs/AI前端全栈教材');
export const contentRoot = path.join(siteRoot, '.content');
const byNumber = new Intl.Collator('zh-CN', { numeric: true });

const definitions = [
  ['01_JavaScript', 'JavaScript', 'JS', '数据类型、函数、原型与异步机制', '前端基础'],
  ['02_TypeScript', 'TypeScript', 'TS', '类型建模、泛型与工程中的类型安全', '前端基础'],
  ['03_HTML_CSS_浏览器_网络', 'HTML / CSS / 浏览器 / 网络', 'WEB', '页面布局、浏览器运行机制与 HTTP', '前端基础'],
  ['04_Vue3_核心', 'Vue 3 核心', 'VUE', '组件、组合式 API、路由与状态管理', '框架与工程'],
  ['10_Vue3_原理与源码', 'Vue 原理与源码', '</>', '从 Vue 2 响应式到 Vue 3 编译与渲染', '框架与工程'],
  ['05_React_RN', 'React / React Native', 'RN', 'React 的运行模型与跨端开发', '框架与工程'],
  ['06_Vite_Nuxt', 'Vite / Nuxt', 'DEV', '构建工具、服务端渲染与全栈框架', '框架与工程'],
  ['09_工程化_性能_安全_系统设计_项目面试', '工程化与项目面试', 'ENG', '测试、性能、安全与系统设计', '框架与工程'],
  ['07_Java_Spring_DB_Redis', 'Java / Spring / 数据库', 'JAVA', '后端基础、接口、事务与缓存', '横向拓展'],
  ['08_AI_LLM_Streaming_Agent_MCP_RAG', 'AI 应用开发', 'AI', '流式交互、Agent、MCP 与 RAG', '横向拓展']
];

function walk(directory) {
  return readdirSync(directory, { withFileTypes: true })
    .sort((a, b) => byNumber.compare(a.name, b.name))
    .flatMap(entry => entry.isDirectory()
      ? walk(path.join(directory, entry.name))
      : [path.join(directory, entry.name)]);
}

export function pageLink(relative) {
  return encodeURI(`/course/${relative.replaceAll('\\', '/').replace(/\.md$/, '.html')}`);
}

function heading(body, fallback) {
  return body.match(/^# (.+)$/m)?.[1]?.trim() ?? fallback.replace(/\.md$/, '').replaceAll('_', ' ');
}

function isChapter(relative) {
  const name = path.basename(relative);
  return /^\d{2}_/.test(name) && !/^(00|98|99)_/.test(name) && !name.includes('知识覆盖清单');
}

export function syncContent() {
  const written = new Set();
  function write(relative, value) {
    const filename = path.resolve(contentRoot, relative);
    if (!filename.startsWith(contentRoot + path.sep)) throw new Error('Invalid generated path');
    written.add(filename);
    mkdirSync(path.dirname(filename), { recursive: true });
    const next = Buffer.isBuffer(value) ? value : Buffer.from(value);
    if (!existsSync(filename) || !readFileSync(filename).equals(next)) writeFileSync(filename, next);
  }

  const files = walk(sourceRoot).filter(file => /\.(md|html|mjs|png|jpg|svg|webp)$/.test(file));
  const pages = files.filter(file => file.endsWith('.md') && path.basename(file) !== '技术路线.md')
    .map(file => {
      const relative = path.relative(sourceRoot, file).replaceAll('\\', '/');
      const body = readFileSync(file, 'utf8').replace(/^\uFEFF/, '');
      return { relative, body, title: heading(body, relative), link: pageLink(relative), chapter: isChapter(relative) };
    });

  const topics = definitions.map(([directory, title, badge, description, group]) => {
    const entries = pages.filter(page => page.relative.startsWith(directory + '/'));
    const chapters = entries.filter(page => page.chapter);
    const overview = entries.find(page => path.posix.dirname(page.relative) === directory && path.posix.basename(page.relative).startsWith('00_'));
    const interview = entries.find(page => path.posix.basename(page.relative).startsWith('99_'));
    const coverage = entries.find(page => path.posix.basename(page.relative).includes('知识覆盖清单'));
    const questions = chapters.reduce((total, page) => total + (page.body.match(/^### .*\[P[012][^\]]*\]/gm)?.length ?? 0), 0);
    return { directory, title, badge, description, group, chapters: chapters.length, questions, link: overview.link, interview: interview.link, coverage: coverage.link };
  });

  for (const page of pages) {
    const topic = topics.find(item => page.relative.startsWith(item.directory + '/'));
    const siblings = pages.filter(item => item.chapter && path.posix.dirname(item.relative) === path.posix.dirname(page.relative));
    const position = siblings.indexOf(page);
    const questionCount = page.body.match(/^### .*\[P[012][^\]]*\]/gm)?.length ?? 0;
    const metadata = {
      title: page.title,
      series: topic?.title ?? '阅读指南',
      seriesLink: topic?.link ?? '/catalog',
      minutes: Math.max(1, Math.ceil(page.body.length / 750)),
      questionCount,
      hasInterview: page.body.includes('id="interview"'),
      prev: page.chapter && siblings[position - 1] ? { text: siblings[position - 1].title, link: siblings[position - 1].link } : false,
      next: page.chapter && siblings[position + 1] ? { text: siblings[position + 1].title, link: siblings[position + 1].link } : false
    };
    // Course prose is content, not executable Vue templates (e.g. {{ name }}).
    const body = page.body.replace(/\]\((examples\/[^)]+)\)/g, (_, asset) =>
      `](<${encodeURI('/course/' + path.posix.dirname(page.relative) + '/' + asset)}>)`);
    write('course/' + page.relative, `---\n${JSON.stringify(metadata, null, 2)}\n---\n\n<div v-pre>\n\n${body}\n\n</div>\n`);
  }

  for (const file of files.filter(file => !file.endsWith('.md'))) {
    write('public/course/' + path.relative(sourceRoot, file), readFileSync(file));
  }

  function sidebarItems(directory) {
    const direct = pages.filter(page => path.posix.dirname(page.relative) === directory)
      .map(page => ({ text: page.title, link: page.link }));
    const children = [...new Set(pages.filter(page => page.relative.startsWith(directory + '/'))
      .map(page => page.relative.slice(directory.length + 1).split('/'))
      .filter(parts => parts.length > 1).map(parts => parts[0]))];
    const extras = direct.filter(item => /覆盖|面试索引/.test(item.text));
    return [...direct.filter(item => !extras.includes(item)), ...children.map(child => ({
      text: child.replace(/^\d+_/, ''), collapsed: false, items: sidebarItems(directory + '/' + child)
    })), ...extras];
  }

  const sidebar = {};
  for (const topic of topics) {
    // VitePress matches decoded paths against sidebar keys.
    sidebar['/course/' + topic.directory + '/'] = [
      { text: '全部专题', link: '/catalog' },
      { text: topic.title, items: sidebarItems(topic.directory) },
      { text: '继续探索', collapsed: true, items: topics.filter(item => item !== topic).map(item => ({ text: item.title, link: item.link })) }
    ];
  }
  sidebar['/'] = [{ text: '阅读入口', items: [
    { text: '全部专题', link: '/catalog' }, { text: '面试索引', link: '/interviews' }, { text: '学习路线', link: '/guide' }
  ] }, { text: '知识专题', items: topics.map(topic => ({ text: topic.title, link: topic.link })) }];

  const total = { chapters: topics.reduce((n, item) => n + item.chapters, 0), questions: topics.reduce((n, item) => n + item.questions, 0) };
  write('catalog.json', JSON.stringify({ topics, total }, null, 2));
  write('index.md', '---\nlayout: home\ntitle: 首页\n---\n');
  write('catalog.md', `---\ntitle: 全部专题\n---\n\n# 全部专题\n\n按知识点查阅正文，按覆盖清单检查遗漏，按面试索引练习回答。\n\n| 专题 | 正文章节 | 分级题目 | 阅读入口 |\n| --- | --- | --- | --- |\n${topics.map(t => `| [${t.title}](${t.link}) | ${t.chapters} | ${t.questions} | [知识覆盖](${t.coverage}) · [面试题目](${t.interview}) |`).join('\n')}\n`);
  write('interviews.md', `---\ntitle: 面试索引\n---\n\n# 面试索引\n\n题目与答案放在各篇正文末尾。这里按专题进入，点击题目即可跳到对应答案。\n\n- **P0：优先掌握**。能够准确解释概念并举例。\n- **P1：常见进阶**。能够推导机制、处理边界和追问。\n- **P2：按岗位深入**。能够说明设计取舍和验证方法。\n\n级别表示复习优先级，不代表某家公司必考。先尝试回答，再阅读答案。\n\n| 专题 | 题目数 | 题目与答案 |\n| --- | --- | --- |\n${topics.map(t => `| ${t.title} | ${t.questions} | [进入题目索引](${t.interview}) |`).join('\n')}\n\n## 怎样练习复述\n\n选择一道题，用“结论 → 机制 → 例子 → 边界”回答；卡住时回到正文，写一个可运行例子，再合上文档重新讲一遍。项目题结合真实经历，说明自己的职责、方案取舍与验证结果。\n`);
  write('guide.md', `---\ntitle: 学习路线\n---\n\n# 学习路线\n\n前端作为主线，Java 和 AI 应用作为横向能力。已有经验的部分通过题目检查，不必从头顺序重读。\n\n## 01 前端基础\n\n[JavaScript](${topics[0].link}) → [TypeScript](${topics[1].link}) → [HTML / CSS / 浏览器 / 网络](${topics[2].link})。\n\n先理解类型、函数、作用域与异步，再把事件循环、渲染和 HTTP 连到实际页面问题。\n\n## 02 框架与工程\n\n[Vue 3 核心](${topics[3].link}) → [Vue 原理与源码](${topics[4].link}) → [Vite / Nuxt](${topics[6].link})。\n\n先能使用组件和响应式 API，再跟踪依赖、调度与渲染。React / RN 根据项目与岗位补充。\n\n[工程化与项目面试](${topics[7].link})贯穿项目：为具体问题选择测试、性能测量与部署方案。\n\n## 03 Java 项目闭环\n\n[Java / Spring / 数据库](${topics[8].link})：基础类型与集合 → HTTP 接口 → 数据库与事务 → 登录与权限 → Redis → 前后端联调。\n\n用一个完整项目练习，每一步以可运行、可验证的结果结束。\n\n## 04 AI 应用开发\n\n具备 HTTP、异步与接口开发基础后进入 [AI 应用开发](${topics[9].link})：模型 API → 流式交互 → 工具调用 → Agent → MCP / RAG → 安全与评估。\n\n## 日常查阅与面试复习\n\n- 查一个概念：使用顶部搜索，输入“闭包”“事务”或“响应式”等关键词。\n- 学一个专题：从[全部专题](/catalog)进入，按左侧章节阅读。\n- 检查遗漏：打开专题的知识覆盖清单。\n- 练习表达：从[面试索引](/interviews)选择题目，先回答，再对照正文。\n\n版本与示例支持范围见[教材说明](${pageLink('README.md')})。\n`);
  write('public/.nojekyll', '');
  write('public/favicon.svg', readFileSync(new URL('./favicon.svg', import.meta.url)));
  // Remove only stale generated files, never the Markdown source directory.
  if (existsSync(contentRoot)) for (const file of walk(contentRoot)) if (!written.has(file)) unlinkSync(file);
  return { topics, total, sidebar, pages: pages.length };
}
