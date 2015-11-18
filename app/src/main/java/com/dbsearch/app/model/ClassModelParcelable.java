package com.dbsearch.app.model;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;


public class ClassModelParcelable  implements Parcelable {
    private ClassModel classModel;

    public ClassModel getClassModel() {
        return classModel;
    }

    public ClassModelParcelable(ClassModel classModel) {
        super();
        this.classModel = classModel;
    }

    private ClassModelParcelable(Parcel in) {
        classModel = new ClassModel();
        classModel.setTitle(in.readString());
        classModel.setAuthor(in.readString());
        classModel.setSummary(in.readString());
        classModel.setPic((Bitmap) in.readParcelable(Bitmap.class.getClassLoader()));
    }

    @Override
    public int describeContents() {
        return 0;
    }


    @Override
    public void writeToParcel(Parcel in, int flags) {
        in.writeString(classModel.getTitle());
        in.writeString(classModel.getAuthor());
        in.writeString(classModel.getSummary());
        in.writeParcelable(classModel.getPic(), PARCELABLE_WRITE_RETURN_VALUE);

    }


    public static final Parcelable.Creator<ClassModelParcelable> CREATOR =  new Parcelable.Creator<ClassModelParcelable>() {

        @Override
        public ClassModelParcelable createFromParcel(Parcel source) {
            return new ClassModelParcelable(source);
        }

        @Override
        public ClassModelParcelable[] newArray(int size) {
            return new ClassModelParcelable[size];
        }
    };
}
