<template>
  <div class="auth-layout">
    <div class="auth-bg">
      <div class="bubble b1"></div><div class="bubble b2"></div><div class="bubble b3"></div>
    </div>
    <div class="auth-container">
      <div class="auth-logo">
        <div class="logo-icon">🌸</div>
        <h1>汐汐的小窝</h1>
        <p>记录每一个珍贵的成长瞬间 💕</p>
      </div>
      <div class="auth-box"><router-view /></div>
    </div>
  </div>
</template>
<style scoped>
.auth-layout { min-height:100vh; background:linear-gradient(135deg,#FFF0F6,#FFE4F0,#F9A8C9); display:flex; align-items:center; justify-content:center; position:relative; overflow:hidden; }
.auth-bg { position:absolute; inset:0; pointer-events:none; }
.bubble { position:absolute; border-radius:50%; background:rgba(249,168,201,0.2); animation:float 6s ease-in-out infinite; }
.b1 { width:300px; height:300px; top:-100px; right:-100px; }
.b2 { width:200px; height:200px; bottom:-80px; left:-80px; animation-delay:-2s; }
.b3 { width:150px; height:150px; top:50%; left:5%; animation-delay:-4s; }
@keyframes float { 0%,100%{transform:translateY(0) scale(1)} 50%{transform:translateY(-20px) scale(1.05)} }
.auth-container { width:100%; max-width:420px; padding:24px; position:relative; z-index:1; }
.auth-logo { text-align:center; margin-bottom:24px; }
.logo-icon { font-size:56px; margin-bottom:8px; }
.auth-logo h1 { font-size:28px; font-weight:700; color:#E91E7A; margin-bottom:6px; }
.auth-logo p { color:#888; font-size:14px; }
.auth-box { background:white; border-radius:20px; padding:32px; box-shadow:0 8px 40px rgba(249,168,201,0.3); }
</style>
