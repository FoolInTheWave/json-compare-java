import {PolymerElement} from '@polymer/polymer/polymer-element.js';
import {html} from '@polymer/polymer/lib/utils/html-tag.js';
import '@vaadin/vaadin-ordered-layout/vaadin-vertical-layout.js';
import '@vaadin/vaadin-icons/vaadin-icons.js';
import '@vaadin/vaadin-text-field/vaadin-text-area.js';
import '../styles/shared-styles.js';

class ErrorPage extends PolymerElement {
    static get template() {
        return html`
        <style include="shared-styles">
            :host {
                display: flex;
                width: 100%;
                height: 100%;
                min-height: 100%;
            }
            #layout {
                align-items: center;
                min-width: 100%;
                height: 100%;
            }
            #frownIcon {
                width: var(--iron-icon-width, 48px);
                height: var(--iron-icon-height, 48px);
            }
            #errorDetailArea {
                display: flex;
                justify-content: center;
                width: 100%;
            }
            vaadin-text-area.max-height {
                max-height: 500px;
                width: 100%;
            }
        </style>
        <vaadin-vertical-layout id="layout">
            <div>
                <h1>
                    <iron-icon id="frownIcon" icon="vaadin:frown-o"></iron-icon>
                    {{message}}
                </h1>
            </div>
            <div id="errorDetailArea">
                <vaadin-text-area class="max-height" label="Error" value="{{errorDetail}}" readonly></vaadin-text-area>
            </div>
        </vaadin-vertical-layout>
    `;
    }

    static get is() {
        return 'error-page'
    }

    static get properties() {
        return {
        }
    }
}

customElements.define(ErrorPage.is, ErrorPage);
