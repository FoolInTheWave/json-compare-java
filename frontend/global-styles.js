// eagerly import theme styles so as we can override them
import '@vaadin/vaadin-lumo-styles/all-imports';

const $_documentContainer = document.createElement('template');

$_documentContainer.innerHTML = `
<custom-style>
  <style>
    html {
    }
  </style>
</custom-style>


<custom-style>
  <style>
    html {
      overflow:hidden;
    }
  </style>
</custom-style>

<dom-module id="app-layout" theme-for="vaadin-app-layout">
  <template>
    <style>
      :host(:not([dir='rtl']):not([overlay])) [part='drawer'] {
        border-right: none;
        box-shadow: var(--lumo-box-shadow-s);
        background-color: var(--lumo-base-color);
        z-index: 1;
      }
      :host([dir='rtl']:not([overlay])) [part='drawer'] {
        border-left: none;
        box-shadow: var(--lumo-box-shadow-s);
        background-color: var(--lumo-base-color);
        z-index: 1;
      }
      [part='navbar'] {
        box-shadow: var(--lumo-box-shadow-s);
      }
    </style>
  </template>
</dom-module>

<dom-module id="dialog-styles" theme-for="vaadin-dialog-overlay">
    <template>
        <style>
            :host([theme~="custom"]) [part~="content"] {
                padding: 0;
            }
        </style>
    </template>
</dom-module>

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
`;

document.head.appendChild($_documentContainer.content);
