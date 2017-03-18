package deputyapp.deputyapp.Network;

import java.util.List;

import deputyapp.deputyapp.DeputyApplication;
import deputyapp.deputyapp.dao.AuthorisationSha1;
import deputyapp.deputyapp.dao.Business;
import deputyapp.deputyapp.dao.DaoMaster;
import deputyapp.deputyapp.dao.DaoSession;
import deputyapp.deputyapp.dao.PrevShiftsData;

public class ModelManager {

    private static final String TAG = ModelManager.class.getSimpleName();
    private static ModelManager instance = null;
    private final DaoSession daoSession;



    private ModelManager() {
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(DeputyApplication.getContext(), "deputy-db", null);
        DaoMaster daoMaster = new DaoMaster(helper.getWritableDatabase());
        daoSession = daoMaster.newSession();
    }

    public static synchronized ModelManager getInstance() {
        if (instance == null) {
            instance = new ModelManager();
        }
        return instance;
    }

    //-----------------------------------------------remove-----------------------------------------
    public synchronized void removeAllAuthSHA(){daoSession.getAuthorisationSha1Dao().deleteAll();}
    public synchronized void removeAllBusinessData(){daoSession.getBusinessDao().deleteAll();}
    public synchronized void removeAllPreviousShifts(){daoSession.getPrevShiftsDataDao().deleteAll();}

    //-----------------------------------------insert-----------------------------------------------
    public synchronized void insertSha1ToDB(AuthorisationSha1 authorisationSha1){
        daoSession.getAuthorisationSha1Dao().insertInTx(authorisationSha1);
    }

    public synchronized void insertBusinessToDB(Business businessData){
        daoSession.getBusinessDao().insertInTx(businessData);
    }

    public synchronized void insertPrevShift(PrevShiftsData prevShiftsData){
        daoSession.getPrevShiftsDataDao().insertInTx(prevShiftsData);
    }

    //--------------------------------------get--------------------------------------------------
    public synchronized List<AuthorisationSha1> getSHA1(){
        return daoSession.getAuthorisationSha1Dao().queryBuilder().list();
    }

    public synchronized Business getBusiness(){
        return daoSession.getBusinessDao().queryBuilder().list().get(0);
    }

    public synchronized List<PrevShiftsData> getPreviousShiftData(){
        return daoSession.getPrevShiftsDataDao().queryBuilder().list();
    }
}

