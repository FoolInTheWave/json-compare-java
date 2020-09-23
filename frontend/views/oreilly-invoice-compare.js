import {PolymerElement} from '@polymer/polymer/polymer-element.js';
import {html} from '@polymer/polymer/lib/utils/html-tag.js';

// Apply custom style to vaadin-grid shadow DOM
import '../styles/custom-vaadin-grid-styles.js';
// Import common styles
import '../styles/shared-styles.js';

import '@vaadin/vaadin-ordered-layout/vaadin-vertical-layout.js';
import '@vaadin/vaadin-icons/vaadin-icons.js';
import '@vaadin/vaadin-grid/vaadin-grid.js';
import '@vaadin/vaadin-grid/vaadin-grid-column.js';
import '@vaadin/vaadin-grid/vaadin-grid-tree-column.js';
import './error-dialog.js';

class OReillyInvoiceCompare extends PolymerElement {
    static get template() {
        return html`
        <style include="shared-styles">
            :host {
                display: flex;
                width: 100%;
                height: 100%;
                min-height: 100%;
            }
            .vertical-container {
                min-width: 100%;
                height: 100%;
            }
            #oReillyInvoiceTreeGrid {
                height: 100%;
            }
        </style>
        <vaadin-vertical-layout class="vertical-container">
          <h1>O'Reilly Invoice Compare</h1>
          <vaadin-grid id="oReillyInvoiceTreeGrid" aria-label="O'Reilly Invoice">
              <vaadin-grid-tree-column path="fieldName" header="Field" item-has-children-path="hasChildren"></vaadin-grid-tree-column>
              <vaadin-grid-column path="field1Value" header="Invoice #{{oReillyInvoiceId1}}" flex-grow="1"></vaadin-grid-column>
              <vaadin-grid-column path="field2Value" header="Invoice #{{oReillyInvoiceId2}}" flex-grow="1"></vaadin-grid-column>
          </vaadin-grid>
        </vaadin-vertical-layout>
        <error-dialog id="errorDialog"></error-dialog>
        `;
    }

    static get is() {
        return 'oreilly-invoice-compare'
    }

    static get properties() {
        return {
            fieldComparisons: {
                type: Array,
                value: []
            }
        }
    }

    afterServerUpdate() {
        this.$server.compareInvoices();
    }

    _initializeGrid() {
        const self = this;
        const grid = this.$.oReillyInvoiceTreeGrid;

        // Convert field comparison JSON string to object
        this.fieldComparisons = JSON.parse(this.fieldComparisonJson);

        // Attach data provider to tree grid
        grid.dataProvider = function (params, callback) {
            const parentItem = params.parentItem ? params.parentItem : null;
            // Slice out only the requested items
            const startIndex = params.page * params.pageSize;
            // Items to include in the response
            let pageItems;
            // Inform grid of the requested tree level's full size
            let treeLevelSize;
            if (parentItem != null) {
                pageItems = parentItem.childFields;
                treeLevelSize = parentItem.childFields.length;
            } else {
                pageItems = self.fieldComparisons.slice(startIndex, startIndex + params.pageSize);
                treeLevelSize = self.fieldComparisons.length;
            }
            callback(pageItems, treeLevelSize);
        };

        // Compare values between invoices, applying highlighting to fields by dynamically setting CSS class of cell
        grid.cellClassNameGenerator = function (column, rowData) {
            let match = rowData.item.match;
            let classes;
            if (!match) {
                classes = "mismatch mismatch-value";
            }
            return classes;
        };
    }
}

customElements.define(OReillyInvoiceCompare.is, OReillyInvoiceCompare);
