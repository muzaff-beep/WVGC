<!-- Home: single SVG drop/pick and inline result -->
<script>
  import { invoke } from "@tauri-apps/api/core";
  import { open, save } from "@tauri-apps/plugin-dialog";
  import { readFile, writeFile } from "@tauri-apps/plugin-fs";
  import { get } from "svelte/store";
  import { settings } from "../lib/settings";
  import FileDropZone from "../components/FileDropZone.svelte";
  import PreviewPane from "../components/PreviewPane.svelte";
  import WatermelonButton from "../components/WatermelonButton.svelte";
  import ProgressBar from "../components/ProgressBar.svelte";

  let state = "idle"; // idle | working | done | error
  let svgBytes = null;
  let svgPreviewPng = null;
  let vdPreviewPng = null;
  let vdXml = "";
  let sourceName = "";
  let errorMsg = "";

  // Report card metrics
  $: vdSizeKb = vdXml ? (new TextEncoder().encode(vdXml).length / 1024).toFixed(1) : "0";
  $: vdLines = vdXml ? vdXml.split("\n").length : 0;

  async function handleFile(bytes, name) {
    svgBytes = bytes;
    sourceName = name;
    state = "working";
    svgPreviewPng = null;
    vdPreviewPng = null;
    vdXml = "";
    try {
      // Convert
      vdXml = await invoke("convert_svg", { svg: Array.from(bytes) });
      // Parallel previews at the user-configured size
      const px = get(settings).previewSize;
      const [sp, vp] = await Promise.all([
        invoke("render_svg_preview", { svg: Array.from(bytes), px }),
        invoke("render_vd_preview", { vdXml, px }),
      ]);
      svgPreviewPng = sp;
      vdPreviewPng = vp;
      state = "done";
    } catch (e) {
      errorMsg = e?.message ?? String(e);
      state = "error";
    }
  }

  async function pickFile() {
    const path = await open({ filters: [{ name: "SVG", extensions: ["svg", "xml"] }], multiple: false });
    if (!path) return;
    const bytes = await readFile(path);
    const name = path.split(/[\\/]/).pop();
    await handleFile(bytes, name);
  }

  async function exportFile() {
    if (!vdXml) return;
    const outName = sourceName.replace(/\.svg$/i, ".xml");
    const path = await save({ defaultPath: outName, filters: [{ name: "VectorDrawable", extensions: ["xml"] }] });
    if (!path) return;
    await writeFile(path, new TextEncoder().encode(vdXml));
  }

  function reset() { state = "idle"; svgBytes = null; svgPreviewPng = null; vdPreviewPng = null; vdXml = ""; sourceName = ""; errorMsg = ""; }
</script>

<section class="home">
  <h1 class="page-title">Convert a single SVG</h1>

  {#if state === "idle"}
    <FileDropZone on:file={(e) => handleFile(e.detail.bytes, e.detail.name)} />
    <WatermelonButton on:click={pickFile} label="Browse for SVG…" variant="primary" />

  {:else if state === "working"}
    <ProgressBar label="Converting {sourceName}…" indeterminate />

  {:else if state === "done"}
    <div class="result-header">
      <span class="success-badge">✓ {sourceName}</span>
      <div class="actions">
        <WatermelonButton on:click={exportFile} label="Export .xml" variant="teal" />
        <WatermelonButton on:click={reset} label="New" variant="outline" />
      </div>
    </div>

    <div class="report-card">
      <div class="report-item">
        <span class="report-value">{vdSizeKb} KB</span>
        <span class="report-label">Output size</span>
      </div>
      <div class="report-divider"></div>
      <div class="report-item">
        <span class="report-value">{vdLines}</span>
        <span class="report-label">XML lines</span>
      </div>
    </div>

    <PreviewPane {svgPreviewPng} {vdPreviewPng} {vdXml} />

  {:else if state === "error"}
    <div class="error-box">
      <p>⚠ Conversion failed</p>
      <p class="error-msg">{errorMsg}</p>
      <WatermelonButton on:click={reset} label="Try again" variant="outline" />
    </div>
  {/if}
</section>

<style>
  .home { max-width: 900px; margin: 0 auto; }
  .page-title { font-size: 22px; font-weight: 700; color: var(--deep-navy); margin-bottom: 24px; }
  .result-header { display: flex; align-items: center; justify-content: space-between; margin-bottom: 20px; }
  .success-badge { font-weight: 600; color: var(--fresh-teal); font-size: 15px; }
  .actions { display: flex; gap: 10px; }

  .report-card {
    display: flex;
    align-items: center;
    background: #fff;
    border: 1px solid var(--border);
    border-radius: var(--radius);
    padding: 18px 28px;
    margin-bottom: 20px;
    gap: 28px;
  }
  .report-item { display: flex; flex-direction: column; gap: 2px; }
  .report-value { font-size: 20px; font-weight: 700; color: var(--deep-navy); }
  .report-label { font-size: 12px; color: var(--slate-gray); text-transform: uppercase; letter-spacing: .04em; }
  .report-divider { width: 1px; height: 36px; background: var(--border); }
  .error-box { background: #fff0f0; border: 1px solid #f5c6c6; border-radius: var(--radius); padding: 24px; }
  .error-box p { color: var(--watermelon-red); font-weight: 600; margin-bottom: 8px; }
  .error-msg { font-family: monospace; font-size: 13px; color: #c0392b; margin-bottom: 16px; }
</style>
