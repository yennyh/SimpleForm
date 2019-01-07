/**
 * Models a SimpleForm class to access form model and runs the form
 * controller/view.
 * 
 * @author yen_my_huynh 1/5/19
 */
public class SimpleForm {
	public static void main(String[] args) {
		MySimpleFormModel mcm = new MySimpleFormModel();
		MySimpleFormView mcv = new MySimpleFormView(mcm);
		mcm.attach(mcv);
	}
}
