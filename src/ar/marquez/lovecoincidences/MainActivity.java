package ar.marquez.lovecoincidences;

import java.util.ArrayList;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import ar.marquez.coincidenciasamorosas.R;

public class MainActivity extends Activity {

	private static final String TAG = "MainActivity";

	private EditText txtName1;
	private EditText txtName2;

	private Button btnFind;
	private Button btnShare;

	private TextView lblPercent;
	private TextView lblPercentData;

	private ArrayList<Character> croppedString;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		handleControls();

		initView();
	}

	private void handleControls() {
		txtName1 = (EditText) findViewById(R.id.txtName1);

		txtName2 = (EditText) findViewById(R.id.txtName2);

		btnFind = (Button) findViewById(R.id.btnFind);
		btnFind.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				btnFindClick();
			}
		});

		lblPercent = (TextView) findViewById(R.id.lblPercent);

		lblPercentData = (TextView) findViewById(R.id.lblPercentData);
	}

	private void initView() {
		lblPercent.setVisibility(TextView.INVISIBLE);
		lblPercentData.setVisibility(TextView.INVISIBLE);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		String title = (String) item.getTitle();
		if (title.equals(getString(R.string.action_share))) {
			btnShareClick();
		}

		return true;
	}

	// ------------------------
	// ------ EVENTOS ---------
	// ------------------------

	private void btnFindClick() {
		if (validateFields() == true) {
			String matchingString = findMatchingLetters(txtName1.getText()
					.toString().toLowerCase(), txtName2.getText().toString()
					.toLowerCase());

			int porcentajeCoincidencia = findPercent(matchingString);

			lblPercentData.setText(String.valueOf(porcentajeCoincidencia)
					+ " %");
			lblPercentData.setVisibility(TextView.VISIBLE);

			lblPercent.setText(calculateText(porcentajeCoincidencia));
			lblPercent.setVisibility(TextView.VISIBLE);
		} else {
			buildIncompleteFieldsDialog();
		}
	}

	private void btnShareClick() {
		if (validateFields() == true) {
			String shareBody = capitalize(txtName1.getText().toString())
					+ " y " + capitalize(txtName2.getText().toString())
					+ " tienen " + lblPercentData.getText().toString()
					+ " de amor, " + lblPercent.getText().toString();

			Intent sharingIntent = new Intent(
					android.content.Intent.ACTION_SEND);
			sharingIntent.setType("text/plain");
			sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,
					"Coincidencias Amorosas");
			sharingIntent
					.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
			startActivity(Intent.createChooser(sharingIntent, "Compartir vía"));
		} else {
			buildIncompleteFieldsDialog();
		}
	}

	// ------------------------
	// ------ HELPERS ---------
	// ------------------------

	private boolean validateFields() {
		String name1 = txtName1.getText().toString();
		String name2 = txtName2.getText().toString();

		return "".equals(name1) == false && name1.length() > 0
				&& "".equals(name2) == false && name2.length() > 0;
	}

	private void buildIncompleteFieldsDialog() {
		AlertDialog myAlertDialog = null;
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Atención");
		builder.setMessage("Por favor complete los campos de los nombres");
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int arg1) {
				dialog.dismiss();
			}
		});
		builder.setCancelable(false);
		myAlertDialog = builder.create();
		myAlertDialog.show();
	}

	private static String capitalize(String str) {
		StringBuilder b = new StringBuilder(str);
		int i = 0;
		do {
			b.replace(i, i + 1, b.substring(i, i + 1).toUpperCase());
			i = b.indexOf(" ", i) + 1;
		} while (i > 0 && i < b.length());

		return b.toString();
	}

	private String calculateText(int porc) {
		String ret = "";
		if (porc >= 70)
			ret = "Que puchurrumines!!!";
		else if (porc >= 50 && porc < 70)
			ret = "Vienen bien!";
		else if (porc >= 30 && porc < 50)
			ret = "Mmm que anda pasando?";
		else if (porc >= 0 && porc < 30)
			ret = "Uff ya no da para mas che..";

		return ret;
	}

	private String findMatchingLetters(String nombre1, String nombre2) {
		String numbers = "";

		croppedString = initCroppedString(nombre1, nombre2);

		while (croppedString.size() > 0) {
			numbers += String.valueOf(countLettersAndDelete(0));
			Log.d(TAG, "numeros: " + numbers);
		}

		return numbers;
	}

	private ArrayList<Character> initCroppedString(String name1, String name2) {
		ArrayList<Character> ret = new ArrayList<Character>();

		for (int i = 0; i < name1.length(); i++) {
			if (name1.charAt(i) != ' ')
				ret.add(new Character(name1.charAt(i)));
		}
		for (int i = 0; i < name2.length(); i++) {
			if (name2.charAt(i) != ' ')
				ret.add(new Character(name2.charAt(i)));
		}

		Log.d(TAG, "cadenaRecortada: " + ret.toString());
		return ret;
	}

	private int countLettersAndDelete(int pos) {
		int cont = 0;
		Character current = new Character(croppedString.get(pos));
		Log.d(TAG, "contarLetrasYEliminar, actual: " + current.toString());

		for (int i = 0; i < croppedString.size(); i++) {
			if (croppedString.get(i).charValue() == current.charValue()) {
				cont++;
			}
		}

		while (croppedString.remove(current))
			;

		croppedString.trimToSize();

		Log.d(TAG, "cadenaRecortada after removed " + String.valueOf(cont)
				+ " chars: " + croppedString.toString());
		return cont;
	}

	private int findPercent(String matchingString) {
		if (matchingString.length() > 2) {
			int idxInf = 0;
			int idxSup = matchingString.length() - 1;

			String ret = "";

			while (idxInf < idxSup) {
				int firstNumber = Integer.parseInt(matchingString.substring(
						idxInf, idxInf + 1));
				int lastNumber = Integer.parseInt(matchingString.substring(
						idxSup, idxSup + 1));

				ret += String.valueOf(firstNumber + lastNumber);

				idxInf++;
				idxSup--;
			}

			if (matchingString.length() % 2 != 0) {
				ret += String.valueOf(matchingString.charAt((matchingString
						.length() - 1) / 2));
			}

			Log.d(TAG, "encontrarPorcentaje: " + matchingString);
			return findPercent(ret);
		}

		Log.d(TAG, "encontrarPorcentaje: " + matchingString);
		return Integer.parseInt(matchingString);
	}
}
