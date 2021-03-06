package net.helix.pendulum.service.transactionpruning.jobs;
import net.helix.pendulum.controllers.TipsViewModel;
import net.helix.pendulum.service.snapshot.Snapshot;
import net.helix.pendulum.service.spentaddresses.SpentAddressesService;
import net.helix.pendulum.service.transactionpruning.TransactionPruner;
import net.helix.pendulum.service.transactionpruning.TransactionPrunerJob;
import net.helix.pendulum.service.transactionpruning.TransactionPrunerJobStatus;
import net.helix.pendulum.service.transactionpruning.TransactionPruningException;
import net.helix.pendulum.storage.Tangle;

/**
 * Implements the most basic functionality that is shared by the different kinds of jobs.
 */
public abstract class AbstractTransactionPrunerJob implements TransactionPrunerJob {
    /**
     * Holds the execution status of the job.
     */
    private TransactionPrunerJobStatus status = TransactionPrunerJobStatus.PENDING;

    /**
     * Holds a reference to the manager of the job that schedules it execution.
     */
    private TransactionPruner transactionPruner;

    /**
     * Ascertains that pruned transactions are recorded as spent addresses where necessary
     */
    protected SpentAddressesService spentAddressesService;

    /**
     * Holds a reference to the tangle object which acts as a database interface.
     */
    private Tangle tangle;

    /**
     * Holds a reference to the manager for the tips (required for removing pruned transactions from this manager).
     */
    private TipsViewModel tipsViewModel;

    /**
     * Holds a reference to the last local or global snapshot that acts as a starting point for the state of ledger.
     */
    private Snapshot snapshot;

    /**
     * {@inheritDoc}
     */
    @Override
    public void setTransactionPruner(TransactionPruner transactionPruner) {
        this.transactionPruner = transactionPruner;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TransactionPruner getTransactionPruner() {
        return transactionPruner;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setTangle(Tangle tangle) {
        this.tangle = tangle;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Tangle getTangle() {
        return tangle;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setTipsViewModel(TipsViewModel tipsViewModel) {
        this.tipsViewModel = tipsViewModel;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TipsViewModel getTipsViewModel() {
        return tipsViewModel;
    }

    @Override
    public void setSpentAddressesService(SpentAddressesService spentAddressesService) {
        this.spentAddressesService = spentAddressesService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setSnapshot(Snapshot snapshot) {
        this.snapshot = snapshot;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Snapshot getSnapshot() {
        return snapshot;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TransactionPrunerJobStatus getStatus() {
        return status;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setStatus(TransactionPrunerJobStatus status) {
        this.status = status;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public abstract void process() throws TransactionPruningException;

    /**
     * {@inheritDoc}
     */
    @Override
    public abstract String serialize();
}