<!-- Root layout: sidebar nav + slot -->
<script>
  import "../app.css";
  import Nav from "../components/Nav.svelte";
  import { settings } from "../lib/settings";
</script>

<div class="shell" class:dark={$settings.darkMode}>
  <Nav />
  <main class="content">
    <slot />
  </main>
</div>

<style>
  .shell {
    display: flex;
    height: 100vh;
    overflow: hidden;
  }
  .content {
    flex: 1;
    overflow-y: auto;
    padding: 28px 32px;
    background: var(--off-white);
  }
</style>
