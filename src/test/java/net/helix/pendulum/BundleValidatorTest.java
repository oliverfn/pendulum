package net.helix.pendulum;

import com.google.common.primitives.Longs;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import net.helix.pendulum.conf.MainnetConfig;
import net.helix.pendulum.controllers.TransactionViewModel;
import net.helix.pendulum.crypto.SpongeFactory;
import net.helix.pendulum.model.Hash;
import net.helix.pendulum.model.HashFactory;
import net.helix.pendulum.model.TransactionHash;
import net.helix.pendulum.model.persistables.Transaction;
import net.helix.pendulum.service.snapshot.SnapshotProvider;
import net.helix.pendulum.service.snapshot.impl.SnapshotProviderImpl;
import net.helix.pendulum.storage.Tangle;
import net.helix.pendulum.storage.rocksdb.RocksDBPersistenceProvider;
import org.bouncycastle.util.encoders.Hex;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;


public class BundleValidatorTest {

    private static final Tangle tangle = new Tangle();
    private static SnapshotProvider snapshotProvider;
    private static TemporaryFolder dbFolder = new TemporaryFolder();
    private static TemporaryFolder logFolder = new TemporaryFolder();

    @BeforeClass
    public static void setUp() throws Exception {
        dbFolder.create();
        logFolder.create();
        tangle.addPersistenceProvider(
                new RocksDBPersistenceProvider(dbFolder.getRoot().getAbsolutePath(),
                        logFolder.getRoot().getAbsolutePath(), 1000, Tangle.COLUMN_FAMILIES,
                        Tangle.METADATA_COLUMN_FAMILY));
        tangle.init();
        snapshotProvider = new SnapshotProviderImpl().init(new MainnetConfig());
    }

    @AfterClass
    public static void shutdown() throws Exception {
        tangle.shutdown();
        snapshotProvider.shutdown();
        dbFolder.delete();
        logFolder.delete();
    }

    @Test
    public void isInconsistentTest() throws Exception {
        String[] hexTxs = {
            "0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000140000000000000000000000000000000000000000000000000000000000000000000000005d092fc000000000000000030000000000000003a62adb85b7350196e0bd72acb9bf588680812f370974e80d05a8fa20c70e2e790000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000",
            "00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000ffffffffffffffe20000000000000000000000000000000000000000000000000000000000000000000000005d092fc000000000000000020000000000000003a62adb85b7350196e0bd72acb9bf588680812f370974e80d05a8fa20c70e2e790000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000",
            "0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000005d092fc000000000000000010000000000000003a62adb85b7350196e0bd72acb9bf588680812f370974e80d05a8fa20c70e2e790000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000",
            "000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000054384869e65174e6645bd9b6e53e4ed0511cdf2faa2a8a681a57f403e075ef5d000000000000000a0000000000000000000000000000000000000000000000000000000000000000000000005d092fc000000000000000000000000000000003a62adb85b7350196e0bd72acb9bf588680812f370974e80d05a8fa20c70e2e790000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000"
        };

        List<TransactionViewModel> transactions = Arrays.stream(hexTxs)
                .map(s -> Hex.decode(s))
                .map(t -> new TransactionViewModel(t, TransactionHash.calculate(SpongeFactory.Mode.S256, t))).map(t -> {
            try {
                t.store(tangle, snapshotProvider.getInitialSnapshot());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return t;
        }).collect(Collectors.toList());
        Assert.assertFalse(BundleValidator.isInconsistent(transactions));
    }

    @Test
    public void validateAllZerosTest() throws Exception {
        String[] hexTxs = {"00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000dd48ad873a2f69ff0d72591e5fecacd67454e73c7883b7f54b560ea6d23c30eeedc2308aa9140a9b69d3272b2cdd1154ca9ef409590dcaff3ccc41022042436438c4f6e87fb9f05b000000005d092fc0000000005d092fc0000000005d092fc00000000000000063000000000000000000000000000000000000000000000000"};

        List<TransactionViewModel> transactions = Arrays.stream(hexTxs)
                .map(s -> Hex.decode(s))
                .map(t -> new TransactionViewModel(t, TransactionHash.calculate(SpongeFactory.Mode.S256, t))).map(t -> {
            try {
                t.store(tangle, snapshotProvider.getInitialSnapshot());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return t;
        }).collect(Collectors.toList());
        Assert.assertTrue(BundleValidator.validate(tangle, snapshotProvider.getInitialSnapshot(), transactions.get(0).getHash()).get(0).size() == transactions.size());
    }

    @Test
    public void validateTest() throws Exception {
        String[] hexTxs = {
            "0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000ac9b724d9b59dfe903e4689d5875cc8812df8f0d01aac479eb5305488ee8fb9200000000000000000000000000000000000000000000000000000000000000000000000000000000000000005d092fc0000000000000000000000000000000012510410cfdbce7a3bae7eacd206c15f070cccd00b146d60d02c15dedaa980c3a308c84e18770a1b6ba61aeba75db1d9afc175ecd94f0eaab01669b41b90784bdb81b94618440e7aaf089cc6330f9eab244c46a3eb860d6a2628f8de2a803a4eb329a4ddebf3b262e000000005d092fc0000000005d092fc0000000005d092fc0000000000000008c000000000000000000000000000000000000000000000000",
            "2dcbcc9d1da9feb422fa00b6d09ef29155eaf13fdcb2ec9bff6ad239d10ffda0487596def2b68d8b27f2d6701637364b36ce04f4757fd2d1b8450e9afe3e974a5850ef01339502ac7673cf209be7b8cafcd2ff9a3f070b77036c445bdc836eeee1c9731aaca673c74e8d3622ee243171c496e95f98ba856349e0f484de9787153ae0b84a6c705b09defd737382193e65c001792b618c3f3823cf52a699e40a0810e9f5c0451e39c5cb9c6eff31fe6abfb5f404f97e3e17bedf21647948e454ade1a7b0ca4f170d31ae9502d8b60215957e474b5f59be2e8902e190324912820c82b4650065a223c69472142231c6f5458717a91260916b5fc166cc8c9e6b580b9a3167cea69adfdd7cf60a5ca75f546652669e7f8351e8608661c2bd6d7fb02324d36b4df5337764ddc7b93680f61e1ef507b7ddb639693971a6b1c0810fc0e9f13488727acfd4071deb067007eb6edb9ae1324475c225b2d4d3ded1c640e86723818c1b1fe00d1d1a0dd5fdee8e39c5ff77075b3b4e79d3b6c3c887d05be986b466006214df2193a59dfc5ec76d2f71f4cd143c0ae64c6651ce88b2551d9ef3ed5f37ec4c50f3ad03ba5d178c250cfedf92a7f9e6054c0d95dc080dae96a035ef4837a9fed6acf06be7543317cfd8d93ba1a2085979365ea34af9d65b9e7af42d466b81245a04938a9d1c2eabf6e22914cde97df147e15b529ab0a17cd043bbb453bdb1a44256853de35d21f65e4832e51dadd899d943a5fabfd1d17761dbf200000000000000000000000000000000000000000000000000000000000000000000000000000000000000005d092fc0000000000000000100000000000000012510410cfdbce7a3bae7eacd206c15f070cccd00b146d60d02c15dedaa980c3aab1454666ed5eacc9ef7edd62db96951719c052d29e78fbc20084856b8209f06b81b94618440e7aaf089cc6330f9eab244c46a3eb860d6a2628f8de2a803a4ebf0fa5c0bff380e19000000005d092fc0000000005d092fc0000000005d092fc000000000000000bb000000000000000000000000000000000000000000000000"};

        List<TransactionViewModel> transactions = Arrays.stream(hexTxs)
                .map(s -> Hex.decode(s))
                .map(t -> new TransactionViewModel(t, TransactionHash.calculate(SpongeFactory.Mode.S256, t))).map(t -> {
            try {
                    t.store(tangle, snapshotProvider.getInitialSnapshot());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            return t;
        }).collect(Collectors.toList());
        Assert.assertTrue(BundleValidator.validate(tangle, snapshotProvider.getInitialSnapshot(), transactions.get(0).getHash()).get(0).size() == transactions.size());
    }

    @Test
    public void validateMultipleSpend() throws Exception {
        InputStream bundleStream = BundleValidatorTest.class.getResourceAsStream("/long-bundle.json");
        Reader reader = new InputStreamReader(bundleStream, StandardCharsets.UTF_8);

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Hash.class, (JsonDeserializer<Hash>)
                        (jsonElement, type, jsonDeserializationContext) -> HashFactory.TRANSACTION.create(jsonElement.getAsString()))
                .create();

        TransactionDTO[] parsed = gson.fromJson(reader, TransactionDTO[].class);

        LinkedList<TransactionViewModel> bundleTransactions =
                Arrays.stream(parsed).map(TransactionDTO::toTxVM).collect(Collectors.toCollection(LinkedList::new));

        LinkedList<TransactionViewModel> sortedTxs = BundleValidator.validateOrder(bundleTransactions);
        BundleValidator.validateValue(sortedTxs);
        BundleValidator.validateBundleHash(sortedTxs);
        BundleValidator.validateSignatures(sortedTxs);
    }

    public static class TransactionDTO extends Transaction {
        String signatureMessageFragment;
        Hash hash;

        public Hash getHash() {
            return hash;
        }

        public void setHash(Hash hash) {
            this.hash = hash;
        }

        public String getSignatureMessageFragment() {
            return signatureMessageFragment;
        }

        public void setSignatureMessageFragment(String signatureMessageFragment) {
            this.signatureMessageFragment = signatureMessageFragment;
        }

        TransactionViewModel toTxVM() {
            bytes = new byte[TransactionViewModel.SIZE];
            //add the signature
            System.arraycopy(Hex.decode(signatureMessageFragment),
                    0,
                    bytes,
                    0,
                    TransactionViewModel.SIGNATURE_MESSAGE_FRAGMENT_SIZE);

            System.arraycopy(address.bytes(), 0,
                    bytes, TransactionViewModel.ADDRESS_OFFSET, TransactionViewModel.ADDRESS_SIZE);

            System.arraycopy(bundle.bytes(), 0, bytes, TransactionViewModel.BUNDLE_OFFSET,
                    TransactionViewModel.BUNDLE_SIZE);

            System.arraycopy(trunk.bytes(), 0, bytes, TransactionViewModel.TRUNK_TRANSACTION_OFFSET,
                    TransactionViewModel.TRUNK_TRANSACTION_SIZE);

            System.arraycopy(branch.bytes(), 0, bytes, TransactionViewModel.BRANCH_TRANSACTION_OFFSET,
                    TransactionViewModel.BRANCH_TRANSACTION_SIZE);

            System.arraycopy(Longs.toByteArray(value), 0, bytes,
                    TransactionViewModel.VALUE_OFFSET, TransactionViewModel.VALUE_SIZE);

            System.arraycopy(bundleNonce.bytes(), 0, bytes,
                    TransactionViewModel.BUNDLE_NONCE_OFFSET, TransactionViewModel.BUNDLE_NONCE_SIZE);


            System.arraycopy(Longs.toByteArray(timestamp), 0, bytes,
                    TransactionViewModel.TIMESTAMP_OFFSET, TransactionViewModel.TIMESTAMP_SIZE);

            System.arraycopy(Longs.toByteArray(currentIndex), 0, bytes,
                    TransactionViewModel.CURRENT_INDEX_OFFSET, TransactionViewModel.CURRENT_INDEX_SIZE);

            System.arraycopy(Longs.toByteArray(lastIndex), 0, bytes,
                    TransactionViewModel.LAST_INDEX_OFFSET, TransactionViewModel.LAST_INDEX_SIZE);

            return new TransactionViewModel(this, hash);
        }
    }
}
