package main.model;

import java.util.*;

import _zem.afu.org.checkerframework.checker.nullness.qual.NonNull;
import _zem.afu.org.checkerframework.checker.nullness.qual.Nullable;

public class Document {
	public static final String DEFAULT_CATEGORY = "NOT_ASSIGNED";

	private List<Feature> mFeatures;
	private String mName;
	private String mCategory;

	public Document(@NonNull String name, @Nullable String category)
	{
		mName = name;
		if(category!=null)
			mCategory = category;
		else
			mCategory = DEFAULT_CATEGORY;

		//mFeatures = new Hashtable<>();
		mFeatures = new ArrayList<>();
	}

	/**Add feature to document. If feature already exist than increse feature count */
	public void addFeature(@NonNull String token)
	{
		Feature f = new Feature(token,1);
		if(mFeatures.contains(f))
			mFeatures.get(mFeatures.indexOf(f)).increaseFreq();
		else
			mFeatures.add(f);
	}

	public void addFeature(@NonNull Feature feature)
	{
		mFeatures.add(feature);
	}

	public boolean removeFeature(Feature feature)
	{
		if(mFeatures.contains(feature))
		{
			mFeatures.remove(feature);
			return true;
		}

		return false;
	}

	public boolean removeFeature(String token)
	{
		for(Feature f:mFeatures)
		{
			if(f.getName().equals(token))
			{
				mFeatures.remove(f);
				return true;
			}
		}

		return false;
	}

	public List<Feature> getFeatures() {
		return mFeatures;
	}

	public String getName() {
		return mName;
	}

	public String getCategory() {
		return mCategory;
	}

	public int getVocabulary()
	{
		return mFeatures.size();
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || getClass() != o.getClass()) return false;
		Document document = (Document) o;
		return mName.equals(document.mName);
	}

	@Override
	public int hashCode() {
		return Objects.hash(mName);
	}
}
