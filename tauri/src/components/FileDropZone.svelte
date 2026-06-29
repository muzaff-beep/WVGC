<!-- Drag-and-drop zone for SVG or ZIP files -->
<script>
  import { createEventDispatcher } from "svelte";
  const dispatch = createEventDispatcher();

  export let accept = ".svg,.xml";

  let dragOver = false;

  function onDrop(e) {
    e.preventDefault();
    dragOver = false;
    const file = e.dataTransfer?.files?.[0];
    if (!file) return;
    readFile(file);
  }

  function onDragOver(e) { e.preventDefault(); dragOver = true; }
  function onDragLeave() { dragOver = false; }

  function readFile(file) {
    const reader = new FileReader();
    reader.onload = () => {
      const bytes = new Uint8Array(reader.result);
      dispatch("file", { bytes, name: file.name });
    };
    reader.readAsArrayBuffer(file);
  }
</script>

<div
  class="dropzone"
  class:drag-over={dragOver}
  on:drop={onDrop}
  on:dragover={onDragOver}
  on:dragleave={onDragLeave}
  role="region"
  aria-label="Drop file here"
>
  <div class="drop-inner">
    <span class="drop-icon">📂</span>
    <p class="drop-label">Drop {accept === ".zip" ? "a ZIP" : "an SVG"} here</p>
    <p class="drop-sub">or use the button below</p>
  </div>
</div>

<style>
  .dropzone {
    border: 2px dashed var(--border);
    border-radius: var(--radius);
    background: var(--surface);
    padding: 48px 32px;
    text-align: center;
    margin-bottom: 20px;
    transition: border-color .2s, background .2s;
    cursor: default;
  }

  .dropzone.drag-over {
    border-color: var(--fresh-teal);
    background: #edf9f7;
  }

  .drop-icon { font-size: 40px; display: block; margin-bottom: 12px; }
  .drop-label { font-size: 16px; font-weight: 600; color: var(--deep-navy); margin-bottom: 4px; }
  .drop-sub { font-size: 13px; color: var(--slate-gray); }
</style>
