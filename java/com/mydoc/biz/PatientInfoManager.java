package com.mydoc.biz;

import android.content.Context;
import android.util.Log;

import com.mydoc.dto.Content;
import com.mydoc.dto.Patient;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OptionalDataException;
import java.io.StreamCorruptedException;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by yanga on 2013/11/02.
 */
public class PatientInfoManager {
    private Context context;
    private static final String TAG = PatientInfoManager.class.getName();
    public PatientInfoManager(Context context) {
        this.context = context;
    }

    public void addPatient(Patient patient){
        List<Patient> patients = getPatients();
        patients.add(patient);
        writeToPatientsFile(patients);
    }

    public List<Patient> getPatients(){
        try {
            FileInputStream fileIn = context.openFileInput ("patients");
            ObjectInputStream in = new ObjectInputStream(fileIn);
            List<Patient> patients = (List<Patient>) in.readObject();
            in.close();
            fileIn.close();
            return patients;
        } catch (ClassNotFoundException e) {
            Log.e(TAG, e.getMessage(), e);
            return new LinkedList<Patient>();
        } catch (OptionalDataException e) {
            Log.e(TAG, e.getMessage(),e);
            return new LinkedList<Patient>();
        } catch (FileNotFoundException e) {
            Log.e(TAG, e.getMessage(),e);
            return new LinkedList<Patient>();
        } catch (StreamCorruptedException e) {
            Log.e(TAG, e.getMessage(),e);
            return new LinkedList<Patient>();
        } catch (IOException e) {
            Log.e(TAG, e.getMessage(),e);
            return new LinkedList<Patient>();
        }
    }

    public void writeToPatientsFile(List<Patient> patients)  {
        try {
            FileOutputStream fileOut = context.openFileOutput("patients", Context.MODE_PRIVATE);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(patients);
            out.close();
            fileOut.close();
        } catch (FileNotFoundException e) {
            Log.e(TAG, e.getMessage(), e);
        } catch (IOException e) {
            Log.e(TAG, e.getMessage(), e);
        }

    }
}
