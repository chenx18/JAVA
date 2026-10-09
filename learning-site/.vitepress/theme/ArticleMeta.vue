<script setup>
import { useData, withBase } from 'vitepress';
const { frontmatter } = useData();
</script>

<template>
  <div v-if="frontmatter.series" class="article-meta">
    <a :href="withBase(frontmatter.seriesLink)">{{ frontmatter.series }}</a>
    <span class="meta-separator" aria-hidden="true">/</span>
    <span>约 {{ frontmatter.minutes }} 分钟</span>
    <a v-if="frontmatter.hasInterview" class="interview-jump" href="#interview">
      文末题目 <span aria-hidden="true">↓</span>
    </a>
  </div>
</template>
