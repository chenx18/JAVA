import { defineConfig } from 'vitepress';
import { resolve, sep } from 'node:path';
import { contentRoot, sourceRoot, syncContent } from './content.mjs';

const content = syncContent();
const base = process.env.SITE_BASE || '/';
if (!base.startsWith('/') || !base.endsWith('/') || base.includes('..')) {
  throw new Error('SITE_BASE must start and end with /, for example /JAVA/');
}

export default defineConfig({
  lang: 'zh-CN',
  title: '前端知识库',
  description: 'JavaScript、Vue 原理、工程化、Java 与 AI 应用的知识查阅和面试复习。',
  base,
  srcDir: contentRoot,
  outDir: process.env.SITE_OUT_DIR,
  cleanUrls: false,
  appearance: false,
  useWebFonts: false,
  head: [['link', { rel: 'icon', type: 'image/svg+xml', href: base + 'favicon.svg' }]],
  markdown: {
    lineNumbers: true,
    theme: { light: 'github-light', dark: 'github-dark' },
    config(md) {
      md.inline.ruler.before('link', 'symbol-method', (state, silent) => {
        const match = state.src.slice(state.pos).match(/^\[Symbol\.(?:iterator|asyncIterator)\]\(\)/);
        if (!match) return false;
        if (!silent) state.push('code_inline', 'code', 0).content = match[0];
        state.pos += match[0].length;
        return true;
      });
      for (const type of ['html_inline', 'html_block']) {
        const original = md.renderer.rules[type];
        md.renderer.rules[type] = (tokens, index, options, env, self) => {
          const html = tokens[index].content;
          const allowed = /^(?:<a id="[\w-]+"><\/a>|<a id="[\w-]+">|<\/a>|<div v-pre>|<\/div>|<KnowledgeHome\s*\/>|<!--[^]*?-->|&ZeroWidthSpace;)\s*$/.test(html);
          return allowed ? original(tokens, index, options, env, self) : md.utils.escapeHtml(html);
        };
      }
      const renderLink = md.renderer.rules.link_open;
      md.renderer.rules.link_open = (tokens, index, options, env, self) => {
        const href = tokens[index].attrGet('href');
        if (href?.includes('/examples/')) {
          if (href.startsWith('/course/')) tokens[index].attrSet('href', base.slice(0, -1) + href);
          tokens[index].attrSet('target', '_blank');
          tokens[index].attrSet('rel', 'noopener');
        }
        return renderLink ? renderLink(tokens, index, options, env, self) : self.renderToken(tokens, index, options);
      };
    }
  },
  themeConfig: {
    logo: '/favicon.svg',
    siteTitle: '前端知识库',
    nav: [
      { text: '知识目录', link: '/catalog', activeMatch: '^/(catalog|course)/?' },
      { text: '面试索引', link: '/interviews' },
      { text: '学习路线', link: '/guide' }
    ],
    sidebar: content.sidebar,
    outline: { level: [2, 3], label: '本页目录' },
    sidebarMenuLabel: '章节目录',
    returnToTopLabel: '返回顶部',
    darkModeSwitchLabel: '外观',
    docFooter: { prev: '上一篇', next: '下一篇' },
    footer: { message: '知识查阅 · 面试准备 · 项目实践', copyright: '前端知识库' },
    notFound: { title: '没有找到这一页', quote: '可以返回首页，或通过搜索寻找相关知识。', linkLabel: '返回首页', linkText: '返回首页' },
    search: {
      provider: 'local',
      options: {
        disableQueryPersistence: true,
        miniSearch: {
          options: {
            tokenize: (text) => Array.from(new Intl.Segmenter('zh-CN', { granularity: 'word' }).segment(text))
              .filter(part => part.isWordLike).map(part => part.segment.toLowerCase())
          },
          searchOptions: { prefix: true, fuzzy: false, combineWith: 'AND' }
        },
        translations: {
          button: { buttonText: '搜索知识', buttonAriaLabel: '搜索知识库' },
          modal: {
            displayDetails: '显示详细内容', resetButtonTitle: '清空搜索', backButtonTitle: '关闭搜索',
            noResultsText: '没有找到相关内容，试试更短的关键词：',
            footer: { selectText: '选择', navigateText: '切换', closeText: '关闭' }
          }
        }
      }
    }
  },
  vite: {
    plugins: [{
      name: 'watch-course-source',
      configureServer(server) {
        server.watcher.add(sourceRoot);
        let timer;
        const onChange = (_, filename) => {
          if (!resolve(filename).startsWith(sourceRoot + sep)) return;
          clearTimeout(timer);
          timer = setTimeout(() => server.restart(), 180);
        };
        server.watcher.on('all', onChange);
        server.httpServer?.once('close', () => { clearTimeout(timer); server.watcher.off('all', onChange); });
      }
    }]
  }
});
