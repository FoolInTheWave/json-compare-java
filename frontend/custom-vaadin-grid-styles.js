import { registerStyles, css } from "@vaadin/vaadin-themable-mixin/register-styles";

// TODO can this be put in global-styles?
registerStyles("vaadin-grid", css`
    /* Styles which will be included in my-element local scope */
    [part~="cell"].mismatch {
        background-color: var(--lumo-error-color-10pct);
        color: var(--lumo-error-text-color);
    }
    .mismatch-value {
        font-style: italic;
    }`
);