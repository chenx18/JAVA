import DefaultTheme from 'vitepress/theme';
import KnowledgeHome from './KnowledgeHome.vue';
import ArticleMeta from './ArticleMeta.vue';
import { h } from 'vue';
import './style.css';

export default {
  extends: DefaultTheme,
  Layout: () => h(DefaultTheme.Layout, null, {
    'doc-before': () => h(ArticleMeta),
    'home-hero-after': () => h(KnowledgeHome)
  })
};
