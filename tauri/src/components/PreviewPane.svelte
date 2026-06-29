<!-- Side-by-side SVG vs VD preview + collapsible XML -->
<script>
  export let svgPreviewPng = null;   // Uint8Array or number[]
  export let vdPreviewPng  = null;
  export let vdXml = "";

  let xmlExpanded = false;

  function toDataUrl(arr) {
    if (!arr) return null;
    const blob = new Blob([new Uint8Array(arr)], { type: "image/png" });
    return URL.createObjectURL(blob);
  }

  $: svgUrl = toDataUrl(svgPreviewPng);
  $: vdUrl  = toDataUrl(vdPreviewPng);
</script>

<div class="preview-row">
  <div class="preview-tile">
    <p class="tile-label">Original SVG</p>
    {#if svgUrl}
      <img src={svgUrl} alt="SVG preview" />
    {:else}
      <div class="placeholder">No preview</div>
    {/if}
  </div>

  <div class="preview-tile">
    <p class="tile-label">Generated VD</p>
    {#if vdUrl}
      <img src={vdUrl} alt="VectorDrawable preview" />
    {:else}
      <div class="placeholder">No preview</div>
    {/if}
  </div>
</div>

{#if vdXml}
  <div class="xml-card">
    <div class="xml-header">
      <span class="xml-title">VectorDrawable XML</span>
      <button class="expand-btn" on:click={() => xmlExpanded = !xmlExpanded}>
        {xmlExpanded ? "Collapse" : "Expand"}
      </button>
    </div>
    {#if xmlExpanded}
      <pre class="xml-body">{vdXml}</pre>
    {/if}
  </div>
{/if}

<style>
  .preview-row { display: flex; gap: 16px; margin-bottom: 20px; }

  .preview-tile {
    flex: 1;
    border: 1px solid var(--border);
    border-radius: var(--radius);
    padding: 14px;
    background: #fff;
    text-align: center;
  }

  .tile-label { font-size: 12px; color: var(--slate-gray); margin-bottom: 10px; font-weight: 500; text-transform: uppercase; letter-spacing: .04em; }

  img { max-width: 100%; max-height: 300px; object-fit: contain; background: #fff; border-radius: 6px; }

  .placeholder { height: 200px; display: flex; align-items: center; justify-content: center; color: var(--slate-gray); font-size: 13px; }

  .xml-card { border: 1px solid var(--border); border-radius: var(--radius); background: #fff; overflow: hidden; }
  .xml-header { display: flex; justify-content: space-between; align-items: center; padding: 12px 16px; border-bottom: 1px solid var(--border); }
  .xml-title { font-weight: 600; font-size: 14px; color: var(--deep-navy); }
  .expand-btn { background: none; color: var(--fresh-teal); font-size: 13px; font-weight: 600; cursor: pointer; border: none; }
  .xml-body { padding: 16px; font-family: "Cascadia Code", "Consolas", monospace; font-size: 12px; color: #2d3748; overflow-x: auto; max-height: 360px; overflow-y: auto; white-space: pre; }
</style>
