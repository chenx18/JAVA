# 前端知识库网站

采用 VitePress：白底文档布局、左侧章节目录、右侧页内目录、中文全文搜索、代码高亮与复制、上一篇 / 下一篇，以及手机端折叠菜单。

正文来自 `../docs/AI前端全栈教材/`，网站只生成发布副本，不改动原笔记。首页、专题目录、章节顺序和题目数量自动生成。

## 1. 在本机阅读

需要 Node.js 22 或更新的 LTS 版本，建议使用 Node.js 24 LTS。

```powershell
cd E:\JAVA\learning-site
npm ci
npm run dev
```

打开终端显示的地址，默认是 `http://127.0.0.1:5173/`。终端保持运行，按 `Ctrl+C` 停止。若端口已被占用，先关闭此前的网站服务，或修改 `package.json` 中的端口。

## 2. 更新文章

仍然编辑 `E:\JAVA\docs\AI前端全栈教材\` 中的 Markdown。开发服务会监听原文变更，重新生成目录并刷新网站；新增、删除或重命名章节也会同步。不要编辑 `.content/`，它是可再生成的中间文件。

- 数字前缀用于章节排序；`00_` 是目录，`98_` 是覆盖清单，`99_` 是面试索引。
- 面试题与答案仍保留在正文末尾，原有题号和锚点链接保留。
- 同系列增加文章不需要手写菜单；增加新专题时，修改 `.vitepress/content.mjs` 中的 `definitions`。
- 站名与导航在 `.vitepress/config.mjs`，首页在 `.vitepress/theme/KnowledgeHome.vue`，配色在 `.vitepress/theme/style.css`。
- 只发布教材及其中的公开示例。`技术路线.md` 是个人分析稿，不加入网站；`DAY*`、`kb`、其他项目与配置文件不会进入发布目录。

文章是静态内容，不执行原文中的 Vue 插值或自定义标签。原始锚点照常生效；原文中未用反引号包裹的 `<name>`、`<Component/>` 按文字显示。`[Symbol.iterator]()` 也按 API 名称显示，避免被误解为空链接。

## 3. 构建与检查

```powershell
npm run build
npm run check
npm run preview
```

预览地址默认 `http://127.0.0.1:4173/`。发布文件位于 `.vitepress/dist/`；检查脚本验证构建后的本地链接、题目锚点、静态资源与内容数量。VitePress 构建时也会检查文档死链。

搜索在浏览器本地执行，不需要搜索服务账号。索引在首次搜索时加载；中文使用 `Intl.Segmenter` 分词，面向支持该 API 的现代浏览器。

## 4. 免费上线：Cloudflare Pages

适合先发布一个能分享的网址。平台免费套餐及限制以官方当前规则为准，个人静态知识库通常无需购买服务器。

1. 按上一节完成构建和检查。
2. 登录 Cloudflare，进入 Workers & Pages，创建 Pages 项目，选择直接上传静态资源。
3. 上传 `.vitepress/dist/` 目录里的完整内容，确保 `index.html` 位于上传内容的根目录。
4. 部署后使用平台分配的 `项目名.pages.dev` 地址；更新文章后重新构建并上传。

也可选择连接 Git 仓库：项目根目录填 `learning-site`，构建命令填 `npm ci && npm run build && npm run check`，输出目录填 `.vitepress/dist`，Node.js 版本设为 24。保留仓库里的 `docs/AI前端全栈教材/`，构建时需要读取它。

不需要填写 `SITE_BASE`；默认 `/` 适合 Pages 的独立站点域名。首次建项目时选择直接上传还是 Git 集成，按平台支持的迁移方式决定。

官方说明：[Cloudflare Pages](https://developers.cloudflare.com/pages/) · [直接上传](https://developers.cloudflare.com/pages/get-started/direct-upload/)

## 5. 免费上线：GitHub Pages

仓库中已准备 `.github/workflows/learning-site.yml`，仅支持手动触发，推送代码本身不会自动发布。

1. 将网站文件与教材提交到 GitHub。公开仓库可使用 GitHub Pages 免费方案；私有仓库的可用性以账号套餐为准。
2. 仓库 Settings → Pages → Build and deployment 选择 GitHub Actions。
3. Actions → Publish learning site → Run workflow。
4. 工作流根据仓库名计算路径，只上传 `learning-site/.vitepress/dist` 中的网站文件。

以现有 `chenx18/JAVA` 仓库为例，默认项目站点地址应为 `https://chenx18.github.io/JAVA/`，**这是启用部署后才会产生的地址，本地构建不代表已上线**。`用户名.github.io` 仓库使用根路径 `/`。

使用自定义域名或不同发布路径时，将 Actions 仓库变量 `SITE_BASE` 设为实际路径。例如自定义域名一般是 `/`；项目路径是 `/JAVA/`。另行在 Pages 配置域名与 DNS。

可以本地检查子路径构建：

```powershell
$env:SITE_BASE = '/JAVA/'
npm run build
npm run check
Remove-Item Env:SITE_BASE
```

切回根路径预览前，重新执行 `npm run build`。

官方说明：[GitHub Pages](https://docs.github.com/en/pages) · [使用 GitHub Actions 部署](https://docs.github.com/en/pages/getting-started-with-github-pages/using-custom-workflows-with-github-pages)

## 6. 免费范围

VitePress 开源免费；静态站点无需 Java 服务、数据库或付费搜索服务。平台提供的子域名可以免费使用，自己购买的域名需要另付域名费。免费托管有流量、构建次数等限制，国内不同网络的访问速度也需要实测，不承诺永久免费或无限额度。

发布正文使用现有原创教材与引用链接，不复制参考站的文章、品牌或图片。网站样式参考了文档站的阅读方式。
