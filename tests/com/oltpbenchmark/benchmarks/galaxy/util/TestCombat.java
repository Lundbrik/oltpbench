package com.oltpbenchmark.benchmarks.galaxy.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Random;

import junit.framework.TestCase;

import org.junit.Test;

import com.oltpbenchmark.benchmarks.galaxy.GalaxyConstants;
import com.oltpbenchmark.benchmarks.galaxy.procedures.Move;
import com.oltpbenchmark.util.Pair;

/**
 * A class that checks the correctness of the Move procedure
 */
public class TestCombat extends TestCase {
    
    private Connection conn;
    private Move combatProc; //dummy change to Combat
    private Random rng;

    private String ships = GalaxyConstants.TABLENAME_SHIPS;
    private String classes = GalaxyConstants.TABLENAME_CLASSES;
    private String solarsystems = GalaxyConstants.TABLENAME_SOLARSYSTEMS;
    private String fitting = GalaxyConstants.TABLENAME_FITTING;
    private String fittings = GalaxyConstants.TABLENAME_FITTINGS;

    public final String createTmpClass = "INSERT INTO " + classes +
            " VALUES (0, 'Test Cruiser', 1000, ?, 2);";
    public final String createTmpShip = "INSERT INTO " + ships + 
            " VALUES (?, ?, ?, 0, 0, ?);";
    public final String createTmpSystem = "INSERT INTO " + solarsystems +
            " VALUES (0, 100000, 100000, 1);";
    public final String createTmpFitting = "INSERT INTO " + fitting +
            " VALUES (?, ?, ?);";
    public final String createTmpFittings = "INSERT INTO " + fittings +
            " VALUES (?, ?, ?);";
    
    public final String deleteTmpClass = "DELETE FROM " + classes +
            " WHERE class_id = 0;";
    public final String deleteTmpShip = "DELETE FROM " + ships +
            " WHERE ship_id = ?;";
    public final String deleteTmpSystem = "DELETE FROM " + solarsystems +
            " WHERE solar_system_id = 0;";
    public final String deleteTmpFitting = "DELETE FROM " + fitting +
            " WHERE fitting_id = ?;";
    public final String deleteTmpFittings = "DELETE FROM " + fittings +
            " WHERE fittings_id = ?;";
    
    public final String getShipCount = "SELECT COUNT(*) FROM " + ships + ";"; //for later use
    public final String getShipHealth = "SELECT health_points FROM " + ships +
            " where ship_id = ?;";
    

    /*
     * TODO create different tests
     * 1on1 fighting
     * 3on4 fighting
     * not enough ships (0 or 1) in solar system
     * fight with only def fittings
     * fight with only atk fittings
     * test with dying ship (hp -> 0)
     */
    

    /**
     * Fills the database with known test values
     * @throws SQLException
     */
    private void createTestValues(int base, int[] shipid, int[] x, int[] y, int[] hp, int[] fitid,
            int[] fittype, int[] fitval, int[] fitsid, int[] fitsship, int[] fitsfitid ) throws SQLException {
        PreparedStatement ps = conn.prepareStatement(createTmpClass);
        ps.setInt(1, base);
        ps.execute();
        ps = conn.prepareStatement(createTmpSystem);
        ps.execute();
        for (int i = 0; i < shipid.length; i++ ) {
            ps = conn.prepareStatement(createTmpShip);
            ps.setInt(1, shipid[i]);
            ps.setInt(2, x[i]);
            ps.setInt(3, y[i]);
            ps.setInt(4, hp[i]);
            ps.addBatch();
        }
        ps.executeBatch();
        for (int i = 0; i < fitid.length; i++ ) {
            ps = conn.prepareStatement(createTmpFitting);
            ps.setInt(1, fitid[i]);
            ps.setInt(2, fittype[i]);
            ps.setInt(3, fitval[i]);
            ps.addBatch();
        }
        ps.executeBatch();
        for (int i = 0; i < fitsid.length; i++ ) {
            ps = conn.prepareStatement(createTmpFittings);
            ps.setInt(1, fitsid[i]);
            ps.setInt(2, fitsship[i]);
            ps.setInt(3, fitsfitid[i]);
            ps.addBatch();
        }
        ps.executeBatch();
    }
    
    private void removeTestValues(int[] shipid, int[] fitid, int[] fitsid) throws SQLException {
        PreparedStatement ps = null;
        for (int i = 0; i < fitsid.length; i++) {
            ps = conn.prepareStatement(deleteTmpFittings);
            ps.setInt(1, fitsid[i]);
            ps.addBatch();
        }
        ps.executeBatch();
        for (int i = 0; i < fitid.length; i++) {
            ps = conn.prepareStatement(deleteTmpFitting);
            ps.setInt(1, fitid[i]);
            ps.addBatch();
        }
        ps.executeBatch();
        for (int i = 0; i < shipid.length; i++) {
            ps = conn.prepareStatement(deleteTmpShip);
            ps.setInt(1, shipid[i]);
            ps.addBatch();
        }
        ps.executeBatch();
        ps = conn.prepareStatement(deleteTmpSystem);
        ps.execute();
        ps = conn.prepareStatement(deleteTmpClass);
        ps.execute();
    }
    
    private void combatDefined(int x, int y) throws SQLException {
        assertEquals("Combat should be successfull", 0,
                combatProc.run(this.conn, 0, x, y, rng)); //TODO change 0 here HERE!
    }
    
    /**
     * Gets the health points from all ships in shipid array
     * @throws SQLException
     */
    private int[] getHealth(int[] shipid) throws SQLException {
        PreparedStatement ps = null;
        ResultSet tmp = null;
        int[] health = new int[shipid.length];
        for (int i = 0; i < shipid.length; i++) {
            ps = conn.prepareStatement(getShipCount);
            ps.setInt(1, shipid[i]);
            tmp = ps.executeQuery();
            try {
                assertTrue("Query should return something", tmp.next());
                health[i] = tmp.getInt(1);
            } finally {
                tmp.close();
            }
        }
        return health;
    }
    
    
    /**
     * Tests simple one ship vs one ship combat
     * @throws SQLException
     */
    public void testCombat(int combatants, int[] fit_type, int[] fit_value)  throws SQLException {
        int shipsize = combatants;
        int fitsize = fit_type.length;
        int fitssize = fit_value.length;
        
        int base = 100;
        int[] shipid = new int[shipsize];
        int[] x = new int[shipsize];
        int[] y = new int[shipsize];
        int[] hp = new int[shipsize];
        int[] fitid = new int[fitsize];       
        int[] fittype = new int[fitsize];
        int[] fitval = new int[fitsize];
        int[] fitsid = new int[fitssize];
        int[] fitsship = new int[fitssize];
        int[] fitsfitid = new int[fitssize];  
        for (int i = 0; i < shipsize; i++) {
            shipid[i] = 133742 + i;
            x[i] = 0 + i;
            y[i] = 0 + i;
            hp[i] = 100;
        }
        for (int i = 0; i < fitsize; i++) {
            fitid[i] = 133742 + i;
        }
        fittype = fit_type;
        fitval = fit_value;
        if (fitsize != 0) {
            for (int i = 0; i < fitssize; i++) {
                fitsid[i] = 133742 + i;
                fitsship[i] = 133742 + (i % shipsize);
                fitsfitid[i] = 133742 + (i % fitsize);
            }
        }
        createTestValues(base, shipid, x, y, hp, fitid, fittype, fitval, fitsid, fitsship, fitsfitid);
        combatDefined(0, shipsize);
        int[] calhp = new int[shipsize];
        for (int i = 0; i < shipsize; i++) {
            /*
             * calculate expected hp here
             */
            calhp[i] = 100; //dummy #
        }
        int[] posthp = getHealth(shipid);
        for (int i = 0; i < shipsize; i++) {
            assertTrue("Ship " + shipid[i] + " have correct hp after a combat with " + shipsize +
                    " combatants",
                    posthp[i] == calhp[i]);
        }
        removeTestValues(shipid, fitid, fitsid);
    }
    
    /**
     * Tests combat where a ship dies
     * @throws SQLException
     */
    public void dyingShip()  throws SQLException {
        /*
         * TODO
         */
    }

    /**
     * Sets the connection and procedure variables, and runs all the tests
     * @param conn The connection to the database
     * @param moveProc The Move procedure
     * @throws SQLException
     */
    @Test
    public void run(Connection conn, Move combatProc, Random rng) throws SQLException {
        this.conn = conn;
        this.combatProc = combatProc;
        this.rng = rng;
        int[] fittype = new int[2];
        int[] fitvalue = new int[4];
        fittype[0] = 0;
        fittype[1] = 1;
        fitvalue[0] = 50;
        fitvalue[1] = 40;
        fitvalue[2] = 60;
        fitvalue[3] = 40;
        testCombat(2, fittype, fitvalue);
        testCombat(7, fittype, fitvalue);
        testCombat(8, fittype, fitvalue);
        testCombat(1, fittype, fitvalue);

        //attack only
        fittype[0] = 0;
        fittype[1] = 0;
        testCombat(2, fittype, fitvalue);
        //defense only
        fittype[0] = 1;
        fittype[1] = 1;
        testCombat(2, fittype, fitvalue);

        /*
         * TODO dyingShip();
         */

    }


}