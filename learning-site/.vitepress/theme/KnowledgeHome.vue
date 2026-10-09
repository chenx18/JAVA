<script setup>
import { withBase } from 'vitepress';
import catalog from '../../.content/catalog.json';

const groups = [
  { name: '前端基础', number: '01', description: '把语言与浏览器的基础串起来。' },
  { name: '框架与工程', number: '02', description: '从日常开发，走到原理与工程决策。' },
  { name: '横向拓展', number: '03', description: '连接后端与 AI，完成应用交付。' }
];
</script>

<template>
  <main class="knowledge-home">
    <section class="home-intro" aria-labelledby="home-title">
      <div class="intro-copy">
        <p class="eyebrow"><span aria-hidden="true"></span> FRONTEND KNOWLEDGE BASE</p>
        <h1 id="home-title">把知识串起来，<br><em>把问题讲清楚。</em></h1>
        <p class="intro-description">以专题建立理解，以实例验证机制。<br>一份用于日常查阅、查漏补缺与面试复习的知识库。</p>
        <div class="home-actions">
          <a class="primary-action" :href="withBase(catalog.topics[0].link)">开始阅读 <span aria-hidden="true">→</span></a>
          <a class="secondary-action" :href="withBase('/interviews.html')">按面试题复习 <span aria-hidden="true">↗</span></a>
        </div>
      </div>
      <aside class="reading-card" aria-label="阅读指南">
        <p class="reading-label">READING GUIDE <span>阅读方式</span></p>
        <ol>
          <li><span>01</span><div><strong>先理解知识</strong><p>概念、机制、实例与边界</p></div></li>
          <li><span>02</span><div><strong>再回答问题</strong><p>题目与答案统一放在文末</p></div></li>
          <li><span>03</span><div><strong>回到实际项目</strong><p>运行示例，说明取舍与验证</p></div></li>
        </ol>
        <a :href="withBase('/guide.html')">查看学习路线 <span aria-hidden="true">→</span></a>
      </aside>
    </section>

    <div class="library-summary">
      <p><strong>{{ catalog.topics.length }}</strong> 个专题 <span>/</span> <strong>{{ catalog.total.chapters }}</strong> 篇正文 <span>/</span> <strong>{{ catalog.total.questions }}</strong> 道分级题目</p>
      <span class="summary-note">先查知识，再练表达</span>
    </div>

    <section v-for="group in groups" :key="group.name" class="topic-section" :aria-labelledby="'group-' + group.number">
      <div class="section-heading">
        <h2 :id="'group-' + group.number"><span>{{ group.number }}</span> {{ group.name }}</h2>
        <p>{{ group.description }}</p>
      </div>
      <div class="topic-grid">
        <a v-for="topic in catalog.topics.filter(item => item.group === group.name)" :key="topic.directory" class="topic-card" :href="withBase(topic.link)">
          <span class="topic-badge" aria-hidden="true">{{ topic.badge }}</span>
          <div class="topic-copy">
            <h3>{{ topic.title }}</h3>
            <p>{{ topic.description }}</p>
            <small>{{ topic.chapters }} 篇正文 <span>·</span> {{ topic.questions }} 道题目</small>
          </div>
          <span class="topic-arrow" aria-hidden="true">↗</span>
        </a>
      </div>
    </section>

    <div class="home-bottom">
      <p>带着问题阅读，让知识回到实践。</p>
      <a :href="withBase('/catalog.html')">查看完整目录 <span aria-hidden="true">→</span></a>
    </div>
  </main>
</template>
