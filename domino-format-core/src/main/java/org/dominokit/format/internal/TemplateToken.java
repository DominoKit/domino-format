package org.dominokit.format.internal;

/**
 * Represents one parsed piece of a token-style template.
 *
 * <p>A token is either raw text that should be copied to the output unchanged or a placeholder that
 * consumes one runtime argument.
 */
public interface TemplateToken {}
