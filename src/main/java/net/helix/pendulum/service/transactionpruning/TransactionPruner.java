package net.helix.pendulum.service.transactionpruning;

/**
 * Represents the manager for the cleanup jobs that are issued by the
 * {@link net.helix.pendulum.service.snapshot.LocalSnapshotManager} in connection with local snapshots and eventually other
 * parts of the code.
 */
public interface TransactionPruner {
    /**
     * This method adds a job to the TransactionPruner, that consequently can be executed by the {@link #processJobs()}
     * method.
     *
     * In addition to adding the jobs to the internal list of jobs that have to be executed, it informs the job about
     * the {@link TransactionPruner}, the {@link net.helix.pendulum.storage.Tangle}, the
     * {@link net.helix.pendulum.controllers.TipsViewModel} and the {@link net.helix.pendulum.service.snapshot.Snapshot} instances
     * that this job is working on.
     *
     * @param job the job that shall be executed
     * @throws TransactionPruningException if anything goes wrong while adding the job
     */
    void addJob(TransactionPrunerJob job) throws TransactionPruningException;

    /**
     * This method executes all jobs that where added to the {@link TransactionPruner} through
     * {@link #addJob(TransactionPrunerJob)}.
     *
     * The jobs will only be executed exactly once before being removed from the {@link TransactionPruner}.
     *
     * @throws TransactionPruningException if anything goes wrong while processing the jobs
     */
    void processJobs() throws TransactionPruningException;

    /**
     * This method saves the current state of the {@link TransactionPruner}, so it can later be restored by
     * {@link #restoreState()}.
     *
     * It is used to maintain the state between the node restarts and pick up pruning where it stopped when the node shut down.
     *
     * @throws TransactionPruningException if anything goes wrong while saving the current state
     */
    void saveState() throws TransactionPruningException;

    /**
     * Restores the state of the {@link TransactionPruner} after being saved before by {@link #saveState()}.
     *
     * It is used to keep the state between the node restarts and pick up pruning where it stopped when the node shut down.
     *
     * @throws TransactionPruningException if anything goes wrong while restoring the state
     */
    void restoreState() throws TransactionPruningException;

    /**
     * This method removes all queued jobs and resets the state of the {@link TransactionPruner}. It can for example be
     * used to cleanup after tests.
     *
     * @throws TransactionPruningException if anything goes wrong while clearing the jobs
     * */
    void clear() throws TransactionPruningException;
}