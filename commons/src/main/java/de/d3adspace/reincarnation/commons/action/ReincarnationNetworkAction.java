/*
 * Copyright (c) 2017 D3adspace
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit
 * persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the
 * Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 *  DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */

package de.d3adspace.reincarnation.commons.action;

public enum ReincarnationNetworkAction {
	ACTION_BROADCAST(0),
	ACTION_UNKNOWN(-1),
	ACTION_REGISTER_CHANNEL(1),
	ACTION_UNREGISTER_CHANNEL(2),
	ACTION_SET_NAME(3),
	ACTION_REQUEST(4);
	
	private final int actionCode;
	
	ReincarnationNetworkAction(int actionCode) {
		this.actionCode = actionCode;
	}
	
	public static ReincarnationNetworkAction getViaId(int actionCode) {
		for (ReincarnationNetworkAction action : values()) {
			if (action.getActionCode() == actionCode) {
				return action;
			}
		}
		
		return ACTION_UNKNOWN;
	}
	
	public int getActionCode() {
		return actionCode;
	}
}
