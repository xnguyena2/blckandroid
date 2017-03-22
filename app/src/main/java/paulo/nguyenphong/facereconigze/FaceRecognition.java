package paulo.nguyenphong.facereconigze;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;


import paulo.nguyenphong.appxblockchainproject.R;
import paulo.nguyenphong.utils.Convert;

public class FaceRecognition {
    private static final String TAG = "EYEVERTIFY";
	public int eigenfacesNumber = 1;
	private int imgW = 180;
	private int imgH = 200;
    Matrix2D[] eigenvectors;
    Context context;

    String enrollFolder, eiGenVectorPath;

	double[] BioVector = new double[imgH];

    public FaceRecognition(Context context) {
        this.context = context;
    }

	private double[] getImageData(String imageFileName) {
		/*
		RenderedImage rendimg = JAI.create("fileload", imageFileName);
		BufferedImage bufferedImage = convertToGray(new RenderedImageAdapter(
				rendimg).getAsBufferedImage());
		int imageWidth = bufferedImage.getWidth();
		int imageHeight = bufferedImage.getHeight();
		double[] inputFace = new double[imageWidth * imageHeight];
		bufferedImage.getData().getPixels(0, 0, imageWidth, imageHeight,
				inputFace);
		*/
		Bitmap bitmap = BitmapFactory.decodeFile("file path");
		int height = bitmap.getHeight();
		int width = bitmap.getWidth();
		int[] pixel = new int[height * width];
		bitmap.getPixels(pixel, 0, width, 0, 0, width, height);
		return Convert.convertDoubleArray(pixel);
	}

	public double getDistance(double[] bio1, double[] bio2){
		double max = 0;
		for (int i = 0; i < bio1.length; i++) {
			//txt += bio1[0][i] + " ";
			double dis = Math.abs(bio1[i] - bio2[i]);
			if (dis > max)
				max = dis;
		}
		return max;
	}

	public double getDistance(double[] bio){
		double max = 0;
		for (int i = 0; i < bio.length; i++) {
			//txt += bio1[0][i] + " ";
			double dis = Math.abs(bio[i] - BioVector[i]);
			if (dis > max)
				max = dis;
		}
		return max;
	}

    private double[] getImageData(Bitmap bitmap) {
		/*
		RenderedImage rendimg = JAI.create("fileload", imageFileName);
		BufferedImage bufferedImage = convertToGray(new RenderedImageAdapter(
				rendimg).getAsBufferedImage());
		int imageWidth = bufferedImage.getWidth();
		int imageHeight = bufferedImage.getHeight();
		double[] inputFace = new double[imageWidth * imageHeight];
		bufferedImage.getData().getPixels(0, 0, imageWidth, imageHeight,
				inputFace);
		*/
        int height = bitmap.getHeight();
        int width = bitmap.getWidth();
        int[] pixel = new int[height * width];
        bitmap.getPixels(pixel, 0, width, 0, 0, width, height);
        return Convert.convertDoubleArray(pixel);
    }

	private EigenvalueDecomposition getEigenvalueDecomposition(
			Matrix2D imagesData) {
		Matrix2D imagesDataTr=imagesData.transpose();
		Matrix2D covarianceMatrix=imagesData.multiply(imagesDataTr);
		EigenvalueDecomposition egdecomp =covarianceMatrix.getEigenvalueDecomposition();
		return egdecomp;
	}

	public void sortEigenVectors(double[] eigenValues,double[][]eigenVectors){
		Hashtable<Double,double[]> table =  new Hashtable<Double,double[]> ();	
		Double[] evals=new Double[eigenValues.length];
		getEigenValuesAsDouble(eigenValues, evals);		
		fillHashtable(eigenValues, eigenVectors, table, evals);
		ArrayList<Double> keylist = sortKeysInReverse(table);				
		updateEigenVectors(eigenVectors, table, evals, keylist);		
		Double[] sortedkeys=new Double[keylist.size()];
		keylist.toArray(sortedkeys);//store the sorted list elements in an array
		//use the array to update the original double[]eigValues
		updateEigenValues(eigenValues, sortedkeys);		
	}
	private void getEigenValuesAsDouble(double[] eigenValue, Double[] evals) {
		for(int i=0;i<eigenValue.length;i++){
			evals[i]=new Double(eigenValue[i]);
		}
	}
	private ArrayList<Double> sortKeysInReverse(
			Hashtable<Double, double[]> table) {
		Enumeration<Double> keys=table.keys();
		ArrayList<Double> keylist=Collections.list(keys);		
		Collections.sort(keylist,Collections.reverseOrder());//largest first
		return keylist;
	}
	private void updateEigenValues(double[] eigenValue, Double[] sortedkeys) {
		for(int i=0;i<sortedkeys.length;i++){
			Double dbl=sortedkeys[i];
			double dblval=dbl.doubleValue();
			eigenValue[i]=dblval;
		}
	}
	private void updateEigenVectors(double[][] eigenVector,
			Hashtable<Double, double[]> table, Double[] evals,
			ArrayList<Double> keylist) {
		for(int i=0;i<evals.length;i++){
			double[] ret=table.get(keylist.get(i));//coumn i
			setColumn(eigenVector,ret,i);//update the double[][]
		}
	}
	private void fillHashtable(double[] eigenValues, double[][] eigenVectors,
			Hashtable<Double, double[]> table, Double[] evals) {
		for(int i=0;i<eigenValues.length;i++){
			Double key=evals[i];
			double[] value=getColumn(eigenVectors ,i);			
			table.put(key,value);
		}
	}	
	private double[] getColumn( double[][] mat, int j ){
		int m = mat.length;
		double[] res = new double[m];
		for ( int i = 0; i < m; ++i ){
			res[i] = mat[i][j];
		}
		return(res);
	}
	private void setColumn(double[][] mat,double[] col,int c){
		int len=col.length;
		for(int row=0;row<len;row++){
			mat[row][c]=col[row];
		}
	}
	public Bitmap[] getGrayScaleImages(List<String> filenames) {
		Bitmap[] bufimgs = new Bitmap[filenames.size()];
		Iterator<String> it = filenames.iterator();
		int i = 0;
		while (it.hasNext()) {
			String fn = it.next();
			File f = new File(fn);
			if (f.isFile()) {
				Bitmap bitmap = BitmapFactory.decodeFile(f.getAbsolutePath());
				bufimgs[i++] = toGrayscale(bitmap);
			}
		}
		return bufimgs;
	}

	public Bitmap toGrayscale(Bitmap bmpOriginal)
	{
		int width, height;
		height = bmpOriginal.getHeight();
		width = bmpOriginal.getWidth();

		Bitmap bmpGrayscale = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
		Canvas c = new Canvas(bmpGrayscale);
		Paint paint = new Paint();
		ColorMatrix cm = new ColorMatrix();
		cm.setSaturation(0);
		ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
		paint.setColorFilter(f);
		c.drawBitmap(bmpOriginal, 0, 0, paint);
		return bmpGrayscale;
	}

	private List<String> getFileNames(String dir, String[] children) {
		List<String> imageFileNames=new ArrayList<String>();
		for (String i : children){			
			String fileName=dir+File.separator+i;
			imageFileNames.add(fileName);
		}
		Collections.sort(imageFileNames);
		return imageFileNames;
	}
	public List<String> parseDirectory(String directoryName,String extension){		
		final String ext="."+extension;
		String[] children=null;
		File directory=new File(directoryName);
		
		if(directory.isDirectory()){
			children=directory.list(new FilenameFilter(){
				public boolean accept(File f,String name){
					return name.endsWith(ext);						
				}					
			});			
		}else{
			throw new RuntimeException(directoryName+" is not a directory");
		}		
		return getFileNames(directoryName, children);
	}

	private void deleteContents(File f) {
		File[] files=f.listFiles();
		for(File i:files){
			delete(i);
		}
	}
	private void delete(File i) {
		if(i.isFile()){
			boolean del=i.delete();
			/*if(del==true){
				debug("deleted:"+i.getName());
			}*/
		}
	}	

	// This is function used for generating biometric vector
	public Matrix2D[] generateBioVec(String dir, String extension)
	{
		List<String> newFileNames = parseDirectory(dir,extension);
	    
		Bitmap[] bufimgs = getGrayScaleImages(newFileNames);
		int imgNumber = newFileNames.size();
		double imgData[][][] = new double[imgNumber][imgH][imgW];
		Matrix2D[] imagesData = new Matrix2D[imgNumber];
		
		for(int i=0;i<bufimgs.length;i++){
			for(int j=0;j<imgH;j++){
				//bufimgs[i].getData().getPixels(0,j,imgW,1,);
                imgData[i][j] = getImageData(bufimgs[i]);
			}
			imagesData[i] = new Matrix2D(imgData[i]);
		}

		//calculate average image
		double[][] avg = new double[imgH][imgW];
		for(int i=0;i<imgH;i++){
			for(int j=0;j<imgW;j++){
				double sum = 0;
				for(int k=0;k<imgNumber;k++){
					sum += imagesData[k].getQuick(i,j);
				}
				avg[i][j] = sum/imgNumber;
			}
		}
		Matrix2D avgFace = new Matrix2D(avg);

		//calculate Gt
		Matrix2D cov = new Matrix2D(imgW, imgW);
		for(int i=0;i<imgNumber;i++){
			double[] data = imagesData[i].flatten();
			Matrix2D temp = new Matrix2D(data, imgH);
			temp.subtract(avgFace);
			Matrix2D tempTR =temp.transpose();
			Matrix2D mul = tempTR.multiply(temp);
			cov.add(mul);
		}
		for(int i=0;i<imgW;i++){
			for(int j=0;j<imgW;j++){
				cov.set(i,j, cov.getQuick(i,j)/imgNumber);
			}
		}

		//calculate eigenvectors
		EigenvalueDecomposition egdecomp = getEigenvalueDecomposition(cov);
		double[] eigenvalues = egdecomp.getEigenValues();
		double[][] eigvectors = egdecomp.getEigenVectors();
		sortEigenVectors(eigenvalues, eigvectors);
		Matrix2D[] eigenvectors = new Matrix2D[eigenfacesNumber];
		for(int i=0;i<eigenfacesNumber;i++){
			eigenvectors[i] = new Matrix2D(eigvectors[i], imgW);
			normalize(eigenvectors[i]);
		}
		return eigenvectors;
	}
	
	public void normalize(Matrix2D mat){
		//This matrix may contains negative numbers!
		int cols = mat.columns();
		int rows = mat.rows();
		double min, max;
		max = Double.MIN_VALUE;
		min = Double.MAX_VALUE;
		for(int i=0; i<rows; i++)
		for(int j=0; j<cols; j++){
			double t = mat.getQuick(i, j);
			max = Math.max(max, t);
			min = Math.min(min, t);
		}
		double abs = Math.max(Math.abs(min),max);
		for(int i=0; i<rows; i++)
		for(int j=0; j<cols; j++){
			double t = mat.getQuick(i, j);
			mat.setQuick(i,j,(t+abs)/2/abs);
		}
	}

	public double matrixDistance(Matrix2D m1, Matrix2D m2, int rows, int cols){
		double sum = 0;
		for(int i=0;i<rows;i++){
			for(int j=0;j<cols;j++){
				double diff = Math.abs(m1.getQuick(i,j) - m2.getQuick(i,j));
				sum += diff*diff;
			}
		}
		return Math.sqrt(sum);
	}
	
	public static String getFileExtension(String filename) {
	    String ext = "";
	    int i = filename.lastIndexOf('.');
	    if (i > 0 &&  i < filename.length() - 1) {
	        ext = filename.substring(i+1).toLowerCase();
	    }
	    return ext;
	}

	private void createFile() {

        try {
            // load cascade file from application resources
            //InputStream is = getResources().openRawResource(R.raw.lbpcascade_frontalface);
            InputStream isEyeCascade = context.getResources().openRawResource(R.raw.eigenvector);
            File vectorDir = context.getDir("Vector", Context.MODE_PRIVATE);
            File enrollmentDir = context.getDir("Enroll", Context.MODE_PRIVATE);
            if (!enrollmentDir.exists())
                enrollmentDir.mkdirs();
            //mCascadeFile = new File(cascadeDir, "lbpcascade_frontalface.xml");
            //FileOutputStream os = new FileOutputStream(mCascadeFile);

            File mEyeCascadeFile = new File(vectorDir, "eigenvector.txt");
            FileOutputStream osEyeCascade = new FileOutputStream(mEyeCascadeFile);

            byte[] buffer = new byte[4096];
            int bytesRead;

            while ((bytesRead = isEyeCascade.read(buffer)) != -1) {
                osEyeCascade.write(buffer, 0, bytesRead);
            }
            isEyeCascade.close();
            osEyeCascade.close();

                        /*mJavaDetector = new CascadeClassifier(mCascadeFile.getAbsolutePath());
                        if (mJavaDetector.empty()) {
                            Log.e(TAG, "Failed to load cascade classifier");
                            mJavaDetector = null;
                        } else*/
            Log.i(TAG, "Loaded vector from " + mEyeCascadeFile.getAbsolutePath());

            enrollFolder = enrollmentDir.getAbsolutePath();
            eiGenVectorPath = mEyeCascadeFile.getAbsolutePath();
            vectorDir.delete();

        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "Failed to load cascade. Exception thrown: " + e);
        }
    }

    public void loadEigenVectors(){
        createFile();
		loadBioVector();
        try (BufferedReader br = new BufferedReader(new FileReader(eiGenVectorPath))) {

            double[][] eigvectors = new double[imgW][imgW];

            String sCurrentLine;
            int rows = 0;
            while ((sCurrentLine = br.readLine()) != null) {
                //System.out.println(sCurrentLine);
                String listNum[] = sCurrentLine.split(" ");
                if(listNum.length>1)
                    for(int i = 0;i<listNum.length;i++){
                        try{
                            if(listNum[i]!=null && listNum[i]!="")
                                eigvectors[rows][i] = Double.parseDouble(listNum[i]);
                        }catch(Exception ex){
                            System.out.println(ex.toString());
                        }
                    }
                rows++;
            }

            eigenvectors = new Matrix2D[eigenfacesNumber];
            for(int i=0;i<eigenfacesNumber;i++){
                eigenvectors[i] = new Matrix2D(eigvectors[i], imgW);
                normalize(eigenvectors[i]);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.i(TAG,"finished load vector!!");
    }

    public void loadBioVector() {

		try (BufferedReader br = new BufferedReader(new FileReader(enrollFolder + "/bio.txt"))) {

			String sCurrentLine;
			sCurrentLine = br.readLine();
			String listNum[] = sCurrentLine.split(" ");
			String tx = "";
			for(int i = 0;i<BioVector.length;i++){
				tx += listNum[i]+"_";
				try{
					if(listNum[i]!=null && listNum[i]!="")
						BioVector[i] = Double.parseDouble(listNum[i]);
				}catch(Exception ex){
					System.out.println(ex.toString());
				}
			}
			//Log.i(TAG,"leng:"+listNum.length+", tx:"+BioVector[BioVector.length-1]);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public double[] generateBiometric(Bitmap face, boolean isSignUp) {
		face = toGrayscale(face);
		double imgDataTest[][] = new double[imgH][imgW];
		Matrix2D imagesDataTest;

		{
			for (int j = 0; j < imgH; j++) {
				int[] dataTemp = new int[imgW];
				face.getPixels(dataTemp, 0, imgW, 0, j, imgW, 1);
				imgDataTest[j] = Convert.convertDoubleArray(dataTemp);
			}
			imagesDataTest = new Matrix2D(imgDataTest);
		}

		Matrix2D projected;
		{
			double[][] res = new double[eigenfacesNumber][imgH];
			for (int j = 0; j < eigenfacesNumber; j++) {
				res[j] = imagesDataTest.multiply(eigenvectors[j]).flatten();
			}
			projected = new Matrix2D(res);
			normalize(projected);
		}

		double[] vectorBio = new double[imgH];
		//output each matrix with name
		try {
			{
				StringBuffer buf = new StringBuffer();//////////////////////////////////----------->change(object[i] + " ");
				for (int j = 0; j < eigenfacesNumber; j++) {
					for (int k = 0; k < imgH; k++) {
						double d = projected.getQuick(j, k);
						if (isSignUp)
							buf.append(d + " ");
						vectorBio[k] = d;
					}
				}
				if (isSignUp) {
					BioVector = vectorBio;
					PrintStream out = new PrintStream(enrollFolder + "/bio.txt");///storage/emulated/0/Download/
					out.println(buf.toString());
					out.close();
				}
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		if(isSignUp)
			Toast.makeText(context, "SignUp finished!!",
					Toast.LENGTH_LONG).show();
		else
			Toast.makeText(context, "Login finished!!",
					Toast.LENGTH_LONG).show();
		return vectorBio;
	}

	public double[][] generateBiometric(String dir, String extension){
		Log.i(TAG,"file:"+dir);
        /*
		List<String> newFileNames = parseDirectory("template","jpg");
		Bitmap[] bufimgs = getGrayScaleImages(newFileNames);
		int imgNumber = newFileNames.size();
		double imgData[][][] = new double[imgNumber][imgH][imgW];
		Matrix2D[] imagesData = new Matrix2D[imgNumber];
		
		for(int i=0;i<bufimgs.length;i++) {
            for (int j = 0; j < imgH; j++) {
                int[] dataTemp = new int[imgW];
                bufimgs[i].getPixels(dataTemp, 0, imgW, 0, j, imgW, 1);
                imgData[i][j] = Convert.convertDoubleArray(dataTemp);
            }
            imagesData[i] = new Matrix2D(imgData[i]);
        }

		//calculate average image
		double[][] avg = new double[imgH][imgW];
		for(int i=0;i<imgH;i++){
			for(int j=0;j<imgW;j++){
				double sum = 0;
				for(int k=0;k<imgNumber;k++){
					sum += imagesData[k].getQuick(i,j);
				}
				avg[i][j] = sum/imgNumber;
			}
		}
		
		Matrix2D avgFace = new Matrix2D(avg);

		//calculate Gt
		Matrix2D cov = new Matrix2D(imgW, imgW);
		for(int i=0;i<imgNumber;i++){
			double[] data = imagesData[i].flatten();
			Matrix2D temp = new Matrix2D(data, imgH);
			temp.subtract(avgFace);
			Matrix2D tempTR =temp.transpose();
			Matrix2D mul = tempTR.multiply(temp);
			cov.add(mul);
		}
		for(int i=0;i<imgW;i++){
			for(int j=0;j<imgW;j++){
				cov.set(i,j, cov.getQuick(i,j)/imgNumber);
			}
		}
		//calculate eigenvectors
		EigenvalueDecomposition egdecomp = getEigenvalueDecomposition(cov);
		double[] eigenvalues = egdecomp.getEigenValues();
		double[][] eigvectors = egdecomp.getEigenVectors();
		sortEigenVectors(eigenvalues, eigvectors);
		Matrix2D[] eigenvectors = new Matrix2D[eigenfacesNumber];
		for(int i=0;i<eigenfacesNumber;i++){
			eigenvectors[i] = new Matrix2D(eigvectors[i], imgW);
			normalize(eigenvectors[i]);
		}
*/
		List<String> testImgs;
		if(extension!=null){
			testImgs = parseDirectory(dir,extension);
		}else {
			testImgs = new ArrayList<String>();
			testImgs.add(dir);
			File temp = new File(dir);
			dir = temp.getParent();
		}
		Bitmap[] bufimgsTest = getGrayScaleImages(testImgs);
		//assign name to people
		String[] object = new String[bufimgsTest.length];
		Iterator<String> it = testImgs.iterator();
		int ii=0;
		while(it.hasNext()){
			String name = it.next();
			name = name.substring(0, name.length()-6);
			object[ii++] = name;									
		}
		int imgNumberTest = testImgs.size();
		double imgDataTest[][][] = new double[imgNumberTest][imgH][imgW];
		Matrix2D[] imagesDataTest = new Matrix2D[imgNumberTest];

		
		for(int i=0;i<imgNumberTest;i++){
			for(int j=0;j<imgH;j++){
                int[] dataTemp = new int[imgW];
				bufimgsTest[i].getPixels(dataTemp,0,imgW,0,j,imgW,1);
                imgDataTest[i][j] = Convert.convertDoubleArray(dataTemp);
			}
			imagesDataTest[i] = new Matrix2D(imgDataTest[i]);
		}
		
		Matrix2D[] projected = new Matrix2D[imgNumberTest];
		for(int i=0;i<imgNumberTest;i++){
			double[][] res = new double[eigenfacesNumber][imgH];
			for(int j=0;j<eigenfacesNumber;j++){
				res[j] = imagesDataTest[i].multiply(eigenvectors[j]).flatten();
			}
			projected[i] = new Matrix2D(res);
			normalize(projected[i]);
		}

		double [][]vectorBio = new double [imgNumberTest][imgH];
		//output each matrix with name
		try{
			if(dir.endsWith("\\") || dir.endsWith("/"))
			{
				dir.subSequence(0, dir.length()-2);
			}
			PrintStream out = new PrintStream(enrollFolder + "/bio.txt");//dir+"/biometric.txt"
			for(int i=0;i<imgNumberTest;i++){
				StringBuffer buf = new StringBuffer();/////testImgs.get(i) + " "/////////////////////////////----------->change(object[i] + " ");
				for(int j=0; j<eigenfacesNumber; j++){
					for(int k=0; k<imgH; k++) { 
						double d = projected[i].getQuick(j, k);
						buf.append(d + " ");
						vectorBio[i][k] = d;
					}
				}
				out.println(buf.toString());
				out.close();
			}
		}catch(IOException ex){
			ex.printStackTrace();
		}
		return vectorBio;
	}
}

class ImageDistanceInfo {	
	int index;
	double value;
	public ImageDistanceInfo(double value, int index) {
		this.value = value;
		this.index = index;
	}
	public int getIndex() {
		return index;
	}
	public double getValue() {
		return value;
	}
}

class MatchResult {
	private String matchFileName;
	private double matchDistance;
	public MatchResult(String matchFileName,double matchDistance){
		this.matchFileName=matchFileName;
		this.matchDistance=matchDistance;
	}
	public String getMatchFileName() {
		return matchFileName;
	}
	public void setMatchFileName(String matchFileName) {
		this.matchFileName = matchFileName;
	}
	public double getMatchDistance() {
		return matchDistance;
	}
	public void setMatchDistance(double matchDistance) {
		this.matchDistance = matchDistance;
	}
	
}
