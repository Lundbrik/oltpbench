package com.oltpbenchmark.benchmarks.eve;

import java.sql.SQLException;

import com.oltpbenchmark.api.Procedure.UserAbortException;
import com.oltpbenchmark.api.TransactionType;
import com.oltpbenchmark.api.Worker;
import com.oltpbenchmark.benchmarks.eve.PhoneCallGenerator.PhoneCalls;
import com.oltpbenchmark.benchmarks.eve.procedures.Eve;
import com.oltpbenchmark.types.TransactionStatus;

public class EveWorker extends Worker {

    private final PhoneCallGenerator switchboard;

    public EveWorker(EveBenchmark benchmarkModule, int id) {
        super(benchmarkModule, id);
        switchboard = new PhoneCallGenerator(0, benchmarkModule.numShips);
    }

    @Override
    protected TransactionStatus executeWork(TransactionType txnType) throws UserAbortException, SQLException {
        assert (txnType.getProcedureClass().equals(Eve.class));
        PhoneCalls call = switchboard.receive();
        Eve proc = getProcedure(Eve.class);
        assert (proc != null);
        proc.run(conn, call.voteId, call.phoneNumber, call.contestantNumber, EveConstants.MAX_VOTES);
        conn.commit();
        return TransactionStatus.SUCCESS;
    }

}
