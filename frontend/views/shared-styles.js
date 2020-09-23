import '@vaadin/vaadin-lumo-styles/typography.js';
import '@vaadin/vaadin-lumo-styles/color.js';

const $_documentContainer = document.createElement('template');

$_documentContainer.innerHTML = `
    <dom-module id="shared-styles">
        <template>
            <style include="lumo-typography lumo-color">
                .grid-container {
                    display: grid;
                    grid-template-columns: 1fr 1fr 1fr;
                    grid-template-rows: 1fr 1fr 1fr;
                    grid-column-gap: 1em;
                    grid-row-gap: 1em;
                    justify-items: stretch;
                    align-items: center;
                }
                .flex-container {
                    display: flex;
                    justify-content: center;
                    align-items: center;
                }
                .center {
                    text-align: center;
                }
                .right {
                    text-align: right;
                }
                .left {
                    text-align: left;
                }
                .medium-height {
                    height: var(--lumo-size-m);
                }
                .error-text {
                    color: var(--lumo-error-text-color);
                }
                .error-background {
                    background-color: var(--lumo-error-color);
                    color: var(--lumo-error-contrast-color);
                }
            </style>
        </template>
    </dom-module>
    <dom-module id="avocado-dialog-styles" theme-for="vaadin-dialog-overlay">
        <template>
            <style>
                :host([theme~="custom"]) [part~="content"] {
                    padding: 0;
                }
            </style>
        </template>
    </dom-module>
`;

document.head.appendChild($_documentContainer.content);
