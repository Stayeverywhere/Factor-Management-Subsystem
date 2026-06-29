<template>
  <div class="panel">
    <div class="panel-head">
      <h2>{{ title }}</h2>
      <span class="muted">正在建设中</span>
    </div>
    <div class="shortcut">该模块已接入路由，后续将继续补充真实业务页面。</div>
  </div>
</template>

<script setup>
defineProps({ title: String })
</script>
