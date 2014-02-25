/*
 * Copyright 2012 Artur Dryomov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ru.ming13.gambit.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.squareup.otto.Subscribe;

import ru.ming13.gambit.R;
import ru.ming13.gambit.bus.BusProvider;
import ru.ming13.gambit.bus.DeckAssembledEvent;
import ru.ming13.gambit.bus.DeckNotSavedEvent;
import ru.ming13.gambit.bus.OperationSavedEvent;
import ru.ming13.gambit.model.Deck;

public class DeckCreationFragment extends Fragment
{
	public static DeckCreationFragment newInstance() {
		return new DeckCreationFragment();
	}

	@Override
	public View onCreateView(LayoutInflater layoutInflater, ViewGroup fragmentContainer, Bundle savedInstanceState) {
		return layoutInflater.inflate(R.layout.fragment_deck_operation, fragmentContainer, false);
	}

	@Subscribe
	public void onOperationSaved(OperationSavedEvent event) {
		saveDeck();
	}

	private void saveDeck() {
		if (isDeckCorrect()) {
			assembleDeck();
		} else {
			showErrorMessage();
		}
	}

	private boolean isDeckCorrect() {
		return !getDeckTitle().isEmpty();
	}

	private String getDeckTitle() {
		return getDeckTitleView().getText().toString().trim();
	}

	private TextView getDeckTitleView() {
		return (TextView) getView().findViewById(R.id.edit_deck_title);
	}

	private void assembleDeck() {
		Deck deck = new Deck(getDeckTitle());

		BusProvider.getBus().post(new DeckAssembledEvent(deck));
	}

	private void showErrorMessage() {
		if (getDeckTitle().isEmpty()) {
			getDeckTitleView().setError(getString(R.string.error_empty_field));
		} else {
			getDeckTitleView().setError(getString(R.string.error_deck_already_exists));
		}
	}

	@Subscribe
	public void onDeckNotSaved(DeckNotSavedEvent event) {
		showErrorMessage();
	}

	@Override
	public void onResume() {
		super.onResume();

		BusProvider.getBus().register(this);
	}

	@Override
	public void onPause() {
		super.onPause();

		BusProvider.getBus().unregister(this);
	}
}