package dev.edumelo.com.nndl_core.nndl.dhall;

import java.util.List;

/**
 * Legacy YAML the current step runner already understands, plus step names
 * cited by {@code entryStep}, a fork or a loop that are not defined.
 */
public record EncodedNndl(String yaml, List<String> missingStepNames) {
}
