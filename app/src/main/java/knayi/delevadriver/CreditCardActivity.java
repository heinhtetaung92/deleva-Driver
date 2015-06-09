package knayi.delevadriver;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.stripe.android.model.Card;


public class CreditCardActivity extends ActionBarActivity implements View.OnClickListener {

    EditText cardno, cardexpmonth, cardexpyear, cardcvv;

    Button btnsave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credit_card);

        cardno = (EditText) findViewById(R.id.card_number);
        cardexpmonth = (EditText) findViewById(R.id.card_expMonth);
        cardexpyear = (EditText) findViewById(R.id.card_expYear);
        cardcvv = (EditText) findViewById(R.id.card_cvv);

        btnsave = (Button) findViewById(R.id.card_btn_save);

        btnsave.setOnClickListener(this);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_credit_card, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {

        Card card = new Card(cardno.getText().toString(), Integer.parseInt(cardexpmonth.getText().toString()), Integer.parseInt(cardexpyear.getText().toString()), cardcvv.getText().toString());

        if(card.validateCard()){
            Toast.makeText(this, "Validate", Toast.LENGTH_SHORT).show();

        }
        else{
            Toast.makeText(this, "InValidate", Toast.LENGTH_SHORT).show();
        }


    }
}
