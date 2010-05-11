package net.deuce.moman.droid;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.*;
import net.deuce.moman.client.model.EnvelopeClient;
import net.deuce.moman.client.service.EnvelopeClientService;
import net.deuce.moman.client.service.NoAvailableServerException;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class Moman extends BaseActivity {

  private static final int SWIPE_MIN_DISTANCE = 120;
  private static final int SWIPE_MAX_OFF_PATH = 250;
  private static final int SWIPE_THRESHOLD_VELOCITY = 200;

  protected static final int MENU_QUIT = 0;
  protected static final int MENU_ACCOUNTS = 1;
  protected static final int MENU_TRANSACTIONS = 2;
  protected static final int MENU_TRANSFER = 3;

  protected static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

  private LinearLayout ll;
  private TextView title;

  private EnvelopeClientService clientService = EnvelopeClientService.instance();

  private boolean needsRedisplay = false;

  private Animation slideLeftIn;
  private Animation slideLeftOut;
  private Animation slideRightIn;
  private Animation slideRightOut;


  @Override
  public void onSaveInstanceState(Bundle savedInstanceState) {
    super.onSaveInstanceState(savedInstanceState);
  }

  /**
   * Called when the activity is first created.
   */
  @Override
  protected void doOnCreate(Bundle savedInstanceState) throws NoAvailableServerException {

//    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

    // test the server

//    if (true) throw new RuntimeException("Weeeee");

    slideLeftIn = AnimationUtils.loadAnimation(this, R.anim.slide_left_in);
    slideLeftOut = AnimationUtils.loadAnimation(this, R.anim.slide_left_out);
    slideRightIn = AnimationUtils.loadAnimation(this, R.anim.slide_right_in);
    slideRightOut = AnimationUtils.loadAnimation(this, R.anim.slide_right_out);

    if (clientService.currentStackSize() == 0) {
      clientService.pushCurrent(clientService.getRootEnvelope());
    }

    ScrollView sv = new ScrollView(this);
    ll = new LinearLayout(this);
    ll.setOrientation(LinearLayout.VERTICAL);
    sv.addView(ll);

    displayEnvelopes();

    setContentView(sv);
  }

  private void displayEnvelopes() throws NoAvailableServerException {

    ll.removeAllViews();

    title = new TextView(this);
    title.setText(getEnvelopeLabel(clientService.peekCurrent()));
    title.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
    title.setTypeface(Typeface.DEFAULT_BOLD);
    title.setTextSize(title.getTextSize() + 2);
    ll.addView(title);

    TableLayout table = new TableLayout(this);
    LayoutUtils.Layout.WidthFill_HeightFill.applyViewGroupParams(table);

    AnimUtils.setLayoutAnim_slideupfrombottom(table, this);

    ll.addView(table);

    for (final EnvelopeClient client : clientService.getChildren(clientService.peekCurrent())) {
      TableRow row = createRow(client);
      LayoutUtils.Layout.WidthWrap_HeightWrap.applyTableLayoutParams(row);
      row.setPadding(2, 2, 2, 2);
      table.addView(row);
    }

  }

  public TableRow createRow(final EnvelopeClient env) {
    TableRow row = new TableRow(this);

    final TextView envelopeButton = new TextView(this);
    envelopeButton.setText(getEnvelopeLabel(env));
    envelopeButton.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
    envelopeButton.setOnClickListener(new View.OnClickListener() {
      public void onClick(View view) {
        try {
          clientService.pushCurrent(env);
          displayEnvelopes();
        } catch (NoAvailableServerException e) {
          throw new RuntimeException(e);
        }
      }
    });
    envelopeButton.setOnLongClickListener(new View.OnLongClickListener() {
      public boolean onLongClick(View view) {
        clientService.setTargetEnvelope(env);
        Intent intent = new Intent(Moman.this, EnvelopeEdit.class);
        startActivity(intent);
        return false;
      }
    });
    envelopeButton.setOnTouchListener(new View.OnTouchListener() {
      public boolean onTouch(View view, MotionEvent motionEvent) {

        GestureDetector.OnGestureListener gestureListener = new GestureDetector.SimpleOnGestureListener() {
          @Override
          public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
              clientService.setTargetEnvelope(env);
              Intent intent = new Intent(Moman.this, EnvelopeEdit.class);
              startActivity(intent);
              return true;
            } else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
            }
            return false;
          }
        };

        return new GestureDetector(gestureListener).onTouchEvent(motionEvent);
      }
    });
    LayoutUtils.Layout.WidthWrap_HeightWrap.applyTableRowParams(envelopeButton);

    /*
    Button envelopeButton = new Button(this);
    envelopeButton.setText(getEnvelopeLabel(env));
    envelopeButton.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
    envelopeButton.setOnClickListener(new View.OnClickListener() {
      public void onClick(View view) {
        try {
          clientService.pushCurrent(env);
          displayEnvelopes();
        } catch (NoAvailableServerException e) {
          throw new RuntimeException(e);
        }
      }
    });
    LayoutUtils.Layout.WidthWrap_HeightWrap.applyTableRowParams(envelopeButton);
    */

    Button editButton = new Button(this);
    editButton.setText("E");
    editButton.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
    editButton.setOnClickListener(new View.OnClickListener() {
      public void onClick(View view) {
        clientService.setTargetEnvelope(env);
        Intent intent = new Intent(Moman.this, EnvelopeEdit.class);
        startActivity(intent);
      }
    });
    editButton.setEnabled(env.isEditable());
    LayoutUtils.Layout.WidthWrap_HeightWrap.applyTableRowParams(editButton);

    Button moveButton = new Button(this);
    moveButton.setText("M");
    moveButton.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
    moveButton.setOnClickListener(new View.OnClickListener() {
      public void onClick(View view) {
        clientService.setTargetEnvelope(env);
        Intent intent = new Intent(Moman.this, EnvelopeSelect.class);
        startActivity(intent);
      }
    });
    moveButton.setEnabled(env.isEditable());
    LayoutUtils.Layout.WidthWrap_HeightWrap.applyTableRowParams(moveButton);

    Button deleteButton = new Button(this);
    deleteButton.setText("X");
    deleteButton.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
    deleteButton.setOnClickListener(new View.OnClickListener() {
      public void onClick(View view) {
        clientService.setTargetEnvelope(env);
        Intent intent = new Intent(Moman.this, EnvelopeDelete.class);
        startActivity(intent);
      }
    });
    deleteButton.setEnabled(env.isEditable());
    LayoutUtils.Layout.WidthWrap_HeightWrap.applyTableRowParams(deleteButton);

    row.addView(envelopeButton);
    row.addView(editButton);
    row.addView(moveButton);
    row.addView(deleteButton);

    return row;
  }

  @Override
  protected void transfer() {
    needsRedisplay = true;
    super.transfer();
  }

  @Override
  protected void onRestart() {
    super.onRestart();

    try {
      if (needsRedisplay) {
        title.setText(getEnvelopeLabel(clientService.peekCurrent()));
        displayEnvelopes();
        needsRedisplay = false;
        return;
      }

      if (clientService.getSelectedEnvelope() != null) {
        clientService.moveEnvelope(clientService.getSelectedEnvelope(), clientService.getTargetEnvelope());
        clientService.setSelectedEnvelope(null);
      }

      if (clientService.getTargetEnvelope() != null) {
        clientService.setTargetEnvelope(null);
        displayEnvelopes();
      }
    } catch (NoAvailableServerException e) {
      throw new RuntimeException(e);
    }

  }

  public void onBackPressed() {
    if (clientService.currentStackSize() > 1) {
      try {
        clientService.popCurrent();
        displayEnvelopes();
      } catch (NoAvailableServerException e) {
        throw new RuntimeException(e);
      }
    } else {
      super.onBackPressed();
    }
  }

}
