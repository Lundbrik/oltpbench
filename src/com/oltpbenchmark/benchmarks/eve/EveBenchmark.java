package com.oltpbenchmark.benchmarks.eve;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.oltpbenchmark.WorkloadConfiguration;
import com.oltpbenchmark.api.BenchmarkModule;
import com.oltpbenchmark.api.Loader;
import com.oltpbenchmark.api.Worker;
import com.oltpbenchmark.benchmarks.eve.procedures.Eve;

public class EveBenchmark extends BenchmarkModule {

    public final int numShips;

    public EveBenchmark(WorkloadConfiguration workConf) {
        super("eve", workConf, true);
        numShips = EveUtil.getScaledNumShips(workConf.getScaleFactor());
    }

    @Override
    protected List<Worker> makeWorkersImpl(boolean verbose) throws IOException {
        List<Worker> workers = new ArrayList<Worker>();
        for (int i = 0; i < workConf.getTerminals(); ++i) {
            workers.add(new EveWorker(this, i));
        }
        return workers;
    }

    @Override
    protected Loader makeLoaderImpl(Connection conn) throws SQLException {
        return new EveLoader(this, conn);
    }

    @Override
    protected Package getProcedurePackageImpl() {
       return Eve.class.getPackage();
    }

}
