package net.helix.pendulum.service.tipselection;

import net.helix.pendulum.model.Hash;

/**
 * Validates consistency of tails.
 */
@FunctionalInterface
public interface WalkValidator {

    /**
     * Validation
     * <p>
     * Checks if a given transaction is a valid tail.
     * </p>
     *
     * @param transactionHash  Transaction hash to validate consistency of.
     * @return  <tt>true</tt> if tail is valid, <tt>false</tt> otherwise.
     * @throws Exception If Validation fails to execute
     */
    boolean isValid(Hash transactionHash) throws Exception;
}
