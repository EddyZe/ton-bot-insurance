import {css, html, LitElement, type TemplateResult} from 'lit';
import { customElement, property, state } from 'lit/decorators.js';

@customElement('data-binding-view')
class DataBindingView extends LitElement {
    @property() src = '';
    @property() width = '';
    @property() height = '';
    @property() maxWidth = '';
    @property() maxHeight = '';


    @state() active = false;

    render(): TemplateResult {
        return html`
            <div class="video-container" style="margin: 0; position: relative">
                <video style="max-width: ${this.maxWidth}; max-height: ${this.maxHeight}"
                       .src=${this.src}
                       controls
                       width=${this.width}
                       height=${this.height}
                       @play=${this._handlePlay}
                       @pause=${this._handlePause}
                >
                </video>
            </div>
            <div class="controls" style="position: absolute; bottom: 10px; left: 10px; right: 10px; display: flex; gap: 10px; align-items: center;">
                <button @click=${this._togglePlay}>
                    <Icon icon="lumo:play"></Icon>
                </button>
            </div>
        `;
    }

    private _togglePlay() {
        const video = this.shadowRoot!.querySelector('video')!;
        video.paused ? video.play() : video.pause();
    }

    private _handlePlay() {
        this.dispatchEvent(new CustomEvent('play'));
    }

    private _handlePause() {
        this.dispatchEvent(new CustomEvent('pause'));
    }

    private _handleVolumeChange(e: Event) {
        const video = this.shadowRoot!.querySelector('video')!;
        video.volume = parseFloat((e.target as HTMLInputElement).value);
    }

    private onClick() {
        this.active = !this.active;
    }
}

export default DataBindingView;