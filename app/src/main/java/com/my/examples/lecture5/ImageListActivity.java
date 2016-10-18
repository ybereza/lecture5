package com.my.examples.lecture5;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.my.examples.lecture5.dummy.DummyContent;

import java.util.List;

/**
 * An activity representing a list of Books. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link ImageDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class ImageListActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Bitmap> {

	/**
	 * Whether or not the activity is in two-pane mode, i.e. running on a tablet
	 * device.
	 */
	private boolean mTwoPane;
	private SimpleItemRecyclerViewAdapter mAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_book_list);

		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		toolbar.setTitle(getTitle());

		FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
		fab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
						.setAction("Action", null).show();
			}
		});

		RecyclerView recyclerView = (RecyclerView)findViewById(R.id.book_list);
		assert recyclerView != null;
		mAdapter = new SimpleItemRecyclerViewAdapter(DummyContent.ITEMS);
		recyclerView.setAdapter(mAdapter);

		if (findViewById(R.id.book_detail_container) != null) {
			mTwoPane = true;
		}
	}

	private void loadBitmapIntoCache(int position) {
		Bundle b = new Bundle();
		int size = UIUtils.convertDpToPixel(48, this);
		b.putBoolean("scaled", true);
		b.putInt("width", size);
		b.putInt("height", size);
		getLoaderManager().initLoader(position, b, this);
	}

	private Bitmap getBitmap(int position) {
		return ImageStorage.getInstance().getBitmapFromCache(position);
	}

	@Override
	public Loader<Bitmap> onCreateLoader(int id, Bundle args) {
		boolean scaled = args.getBoolean("scaled", false);
		if (scaled) {
			Integer width = args.getInt("width");
			Integer height = args.getInt("height");

			return new BitmapLoader(this, id, scaled, width, height);
		}
		return new BitmapLoader(this, id, false, 0, 0);
	}

	@Override
	public void onLoadFinished(Loader<Bitmap> loader, Bitmap data) {
		ImageStorage.getInstance().putBitmapIntoCache(loader.getId(), data);
		mAdapter.notifyItemChanged(loader.getId());
	}

	@Override
	public void onLoaderReset(Loader<Bitmap> loader) {

	}

	public class SimpleItemRecyclerViewAdapter
			extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

		private final List<DummyContent.DummyItem> mValues;

		public SimpleItemRecyclerViewAdapter(List<DummyContent.DummyItem> items) {
			mValues = items;
		}

		@Override
		public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
			View view = LayoutInflater.from(parent.getContext())
					.inflate(R.layout.book_list_content, parent, false);
			return new ViewHolder(view);
		}

		@Override
		public void onBindViewHolder(final ViewHolder holder, int position) {
			holder.mItem = mValues.get(position);
			Bitmap b = getBitmap(position);
			if (b != null) {
				holder.mIdView.setImageBitmap(b);
			}
			else {
				loadBitmapIntoCache(position);
			}
			holder.mContentView.setText(mValues.get(position).content);

			holder.mView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (mTwoPane) {
						Bundle arguments = new Bundle();
						arguments.putString(ImageDetailFragment.ARG_ITEM_ID, holder.mItem.id);
						ImageDetailFragment fragment = new ImageDetailFragment();
						fragment.setArguments(arguments);
						getSupportFragmentManager().beginTransaction()
								.replace(R.id.book_detail_container, fragment)
								.commit();
					} else {
						Context context = v.getContext();
						Intent intent = new Intent(context, ImageDetailActivity.class);
						intent.putExtra(ImageDetailFragment.ARG_ITEM_ID, holder.mItem.id);

						context.startActivity(intent);
					}
				}
			});
		}

		@Override
		public int getItemCount() {
			return mValues.size();
		}

		public class ViewHolder extends RecyclerView.ViewHolder {
			public final View mView;
			public final ImageView mIdView;
			public final TextView mContentView;
			public DummyContent.DummyItem mItem;

			public ViewHolder(View view) {
				super(view);
				mView = view;
				mIdView = (ImageView) view.findViewById(R.id.id);
				mContentView = (TextView) view.findViewById(R.id.content);
			}

			@Override
			public String toString() {
				return super.toString() + " '" + mContentView.getText() + "'";
			}
		}
	}
}
