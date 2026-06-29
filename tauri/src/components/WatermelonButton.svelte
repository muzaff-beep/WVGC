<!-- Themed button: variants = primary | teal | outline -->
<script>
  import { createEventDispatcher } from "svelte";
  export let label = "Button";
  export let variant = "primary"; // primary | teal | outline
  export let disabled = false;
  const dispatch = createEventDispatcher();
</script>

<button
  class="wm-btn {variant}"
  {disabled}
  on:click={() => dispatch("click")}
>
  {label}
</button>

<style>
  .wm-btn {
    padding: 10px 22px;
    border-radius: 50px;
    font-size: 14px;
    font-weight: 600;
    transition: opacity .15s, box-shadow .15s;
    letter-spacing: .01em;
  }

  .wm-btn:disabled { opacity: .45; cursor: not-allowed; }
  .wm-btn:not(:disabled):hover { box-shadow: 0 3px 10px rgba(0,0,0,.15); }

  .primary  { background: var(--watermelon-red); color: #fff; }
  .teal     { background: var(--fresh-teal);     color: #fff; }
  .outline  { background: transparent; color: var(--deep-navy); border: 1.5px solid var(--border); }
  .outline:hover { border-color: var(--fresh-teal); color: var(--fresh-teal); }
</style>
