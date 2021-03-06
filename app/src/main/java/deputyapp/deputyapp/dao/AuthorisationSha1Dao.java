package deputyapp.deputyapp.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

import deputyapp.deputyapp.dao.AuthorisationSha1;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "AUTHORISATION_SHA1".
*/
public class AuthorisationSha1Dao extends AbstractDao<AuthorisationSha1, Void> {

    public static final String TABLENAME = "AUTHORISATION_SHA1";

    /**
     * Properties of entity AuthorisationSha1.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Sha1 = new Property(0, String.class, "sha1", false, "SHA1");
    };


    public AuthorisationSha1Dao(DaoConfig config) {
        super(config);
    }
    
    public AuthorisationSha1Dao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"AUTHORISATION_SHA1\" (" + //
                "\"SHA1\" TEXT);"); // 0: sha1
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"AUTHORISATION_SHA1\"";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, AuthorisationSha1 entity) {
        stmt.clearBindings();
 
        String sha1 = entity.getSha1();
        if (sha1 != null) {
            stmt.bindString(1, sha1);
        }
    }

    /** @inheritdoc */
    @Override
    public Void readKey(Cursor cursor, int offset) {
        return null;
    }    

    /** @inheritdoc */
    @Override
    public AuthorisationSha1 readEntity(Cursor cursor, int offset) {
        AuthorisationSha1 entity = new AuthorisationSha1( //
            cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0) // sha1
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, AuthorisationSha1 entity, int offset) {
        entity.setSha1(cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0));
     }
    
    /** @inheritdoc */
    @Override
    protected Void updateKeyAfterInsert(AuthorisationSha1 entity, long rowId) {
        // Unsupported or missing PK type
        return null;
    }
    
    /** @inheritdoc */
    @Override
    public Void getKey(AuthorisationSha1 entity) {
        return null;
    }

    /** @inheritdoc */
    @Override    
    protected boolean isEntityUpdateable() {
        return true;
    }
    
}
