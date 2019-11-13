package com.samagra.odktest.tasks;

import android.os.AsyncTask;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.samagra.odktest.data.models.School;

import org.odk.collect.android.application.Collect;

import java.io.File;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import me.xdrop.fuzzywuzzy.FuzzySearch;
import timber.log.Timber;

// TODO : Add Documentation
public class SearchSchoolTask extends AsyncTask<String, Void, List<School>> {

    String searchString;

    public SearchSchoolTask(String searchString) {
        this.searchString = searchString;
    }

    @Override
    protected List<School> doInBackground(String... strings) {
        Timber.e("Starting the search");

        // Load data file and convert to a string of search values
        File dataFile = new File(Collect.ODK_ROOT + "/data.json");
        try {
            JsonReader reader = new JsonReader(new FileReader(dataFile));

            Gson gson = new GsonBuilder()
                    .enableComplexMapKeySerialization()
                    .setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE)
                    .setPrettyPrinting()
                    .setVersion(1.0)
                    .create();

            Type listType = new TypeToken<ArrayList<School>>() {
            }.getType();
            List<School> data = gson.fromJson(reader, listType);
            Timber.e("%s", data.size());

            ArrayList<String> districts = new ArrayList<>();
            ArrayList<String> blocks = new ArrayList<>();
            ArrayList<String> clusters = new ArrayList<>();
            ArrayList<String> villages = new ArrayList<>();
            ArrayList<String> udises = new ArrayList<>();
            ArrayList<String> schoolNames = new ArrayList<>();
            for (int i = 0; i < data.size(); i++) {
                districts.add(data.get(i).district);
                blocks.add(data.get(i).block);
                clusters.add(data.get(i).cluster);
                villages.add(data.get(i).village);
                udises.add(data.get(i).udise);
                schoolNames.add(data.get(i).schoolName);
            }

            districts = makeUnique(districts);
            blocks = makeUnique(blocks);
            clusters = makeUnique(clusters);
            villages = makeUnique(villages);
            udises = makeUnique(udises);
            schoolNames = makeUnique(schoolNames);

            Timber.e("Calculating top results");
            List results1 = FuzzySearch.extractTop(searchString, districts, 10);
            Timber.e("Extract top results found: %s", results1.toString());
            List results2 = FuzzySearch.extractTop(searchString, blocks, 10);
            Timber.e("Extract top results found: %s", results2.toString());
            List results3 = FuzzySearch.extractTop(searchString, clusters, 10);
            Timber.e("Extract top results found: %s", results3.toString());
            List results4 = FuzzySearch.extractTop(searchString, villages, 10);
            Timber.e("Extract top results found: %s", results4.toString());
            List results5 = FuzzySearch.extractTop(searchString, udises, 10);
            Timber.e("Extract top results found: %s", results5.toString());
            List results6 = FuzzySearch.extractTop(searchString, schoolNames, 10);
            Timber.e("Extract top results found: %s", results6.toString());

            double[] ratios = new double[data.size()];
            ArrayList<Double> ratiosFiltered = new ArrayList<Double>();

            for (int i = 0; i < data.size(); i++) {
                int ratio = FuzzySearch.tokenSetPartialRatio(searchString, data.get(i).getStringForSearch());
                ratios[i] = ratio;
            }
            Timber.e("Ratios Calculated");
        } catch (Exception e) {
            Timber.e(e);
        }

        return null;
    }

    public static ArrayList<String> makeUnique(ArrayList<String> districts) {
        Set<String> set = new HashSet<>(districts);
        districts.clear();
        districts.addAll(set);
        Collections.sort(districts);
        return districts;
    }
}
