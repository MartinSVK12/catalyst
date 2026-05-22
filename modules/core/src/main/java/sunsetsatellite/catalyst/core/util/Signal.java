package sunsetsatellite.catalyst.core.util;

import sunsetsatellite.catalyst.Catalyst;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

//sorry deus, but i'm not making a dependency on another mod for a single class
public class Signal<T> {

	private final List<Listener<T>> listeners = new ArrayList<>();
	private final List<Listener<T>> removeQueue = new ArrayList<>();
	private boolean emitting = false;
	public boolean silenced = false;
	public boolean consumed = false;
	public String name;

	public Signal(String name) {
		this.name = name;
	}

	@FunctionalInterface
	public interface Listener<T> {
		void signalEmitted(Signal<T> signal, T t);
	}

	public List<Listener<T>> getListeners() {
		return Collections.unmodifiableList(listeners);
	}

	public void connect(Listener<T> listener) {
		if (!listeners.contains(listener)) {
			listeners.add(listener);
		}
	}

	public void disconnect(Listener<T> listener) {
		if (!emitting) {
			listeners.remove(listener);
		} else {
			removeQueue.add(listener);
		}
	}

	public void consume(){
		if(consumed){
			Catalyst.LOGGER.warn("Signal {} was already consumed!", name);
			return;
		}
		consumed = true;
	}

	public void emit(T t) {
		if (!silenced) {
			emitting = true;
			for (Listener<T> listener : new ArrayList<>(listeners)) {
				listener.signalEmitted(this, t);
				if(consumed) break;
			}
			for (Listener<T> listener : removeQueue) {
				listeners.remove(listener);
			}
			consumed = false;
			removeQueue.clear();
			emitting = false;
		}
	}
}
