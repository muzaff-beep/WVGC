<!-- Sidebar navigation -->
<script>
  import { page } from "$app/stores";
</script>

<nav class="sidebar">
  <div class="logo">
    <span class="logo-wm">🍉</span>
    <span class="logo-text">Watermelon<br/>Converter</span>
  </div>

  <ul class="nav-links">
    <li class:active={$page.url.pathname === "/"}>
      <a href="/">
        <span class="icon">⚡</span>
        <span>Convert</span>
      </a>
    </li>
    <li class:active={$page.url.pathname.startsWith("/batch")}>
      <a href="/batch">
        <span class="icon">📦</span>
        <span>Batch</span>
      </a>
    </li>
    <li class:active={$page.url.pathname.startsWith("/settings")}>
      <a href="/settings">
        <span class="icon">⚙️</span>
        <span>Settings</span>
      </a>
    </li>
  </ul>

  <div class="sidebar-footer">
    <span class="version">v0.1.0</span>
    <span class="studio">Digital Raven</span>
  </div>
</nav>

<style>
  .sidebar {
    width: 200px;
    min-width: 200px;
    background: var(--deep-navy);
    display: flex;
    flex-direction: column;
    padding: 24px 0;
    height: 100vh;
  }

  .logo {
    display: flex;
    align-items: center;
    gap: 10px;
    padding: 0 20px 28px;
    border-bottom: 1px solid rgba(255,255,255,.12);
    margin-bottom: 16px;
  }

  .logo-wm { font-size: 28px; }
  .logo-text { color: #fff; font-size: 13px; font-weight: 600; line-height: 1.4; }

  .nav-links { list-style: none; flex: 1; }

  .nav-links li a {
    display: flex;
    align-items: center;
    gap: 10px;
    padding: 11px 20px;
    color: rgba(255,255,255,.65);
    text-decoration: none;
    font-size: 14px;
    font-weight: 500;
    transition: background .15s, color .15s;
    border-radius: 6px;
    margin: 2px 8px;
  }

  .nav-links li.active a,
  .nav-links li a:hover {
    background: rgba(255,255,255,.1);
    color: #fff;
  }

  .nav-links li.active a { color: #5CE0D0; }

  .icon { font-size: 16px; width: 20px; text-align: center; }

  .sidebar-footer {
    padding: 16px 20px 0;
    border-top: 1px solid rgba(255,255,255,.1);
    display: flex;
    flex-direction: column;
    gap: 2px;
  }

  .version { color: rgba(255,255,255,.35); font-size: 11px; }
  .studio  { color: rgba(255,255,255,.45); font-size: 12px; font-weight: 500; }
</style>
