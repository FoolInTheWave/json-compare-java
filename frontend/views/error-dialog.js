// TODO convert to LitElement
import {PolymerElement} from '@polymer/polymer/polymer-element.js';
import {html} from '@polymer/polymer/lib/utils/html-tag.js';
import '@vaadin/vaadin-dialog/vaadin-dialog.js';
import '@vaadin/vaadin-button/vaadin-button.js';
import '../styles/shared-styles.js';

class ErrorDialog extends PolymerElement {
    static get template() {
        return html`
        <style include="shared-styles dialog-styles">
            #dialogGrid {
                grid-template-columns: auto;
                grid-template-rows: auto;
                align-items: start;
            }
            #dialogContentContainer {
                padding: 0.5em 1em 0 1em;
                max-width: 20em;
            }
            #dialogButtonContainer {
                padding-bottom: 0.5em;
            }
        </style>
        <vaadin-dialog no-close-on-esc="" no-close-on-outside-click="" id="dialog" theme="custom">
            <template>
                <div id="dialogGrid" class="grid-container">
                    <div class="flex-container medium-height error-background">
                        <label>Error</label>
                    </div>
                    <div id="dialogContentContainer" class="center">
                        <span>{{message}}</span>
                    </div>
                    <div id="dialogButtonContainer" class="center">
                        <vaadin-button id="dialogButton" theme="secondary" on-click="_handleButtonClick">OK</vaadin-button>
                    </div>
                </div>
            </template>
        </vaadin-dialog>
    `;
    }

    static get is() {
        return 'error-dialog'
    }

    static get properties() {
        return {
            message: String
        }
    }

    open(message, callback) {
        if (!this.$.dialog.opened) {
            this.message = message;
            // set optional user defined callback
            if (typeof callback === 'function') {
                this._onClose = callback;
            }
            this.$.dialog.opened = true;
        }
    }

    _handleButtonClick(event) {
        this.$.dialog.opened = false;
        this._onClose();
    }

    // Placeholder function for user defined callback
    _onClose() {
        return;
    }
}

customElements.define(ErrorDialog.is, ErrorDialog);
