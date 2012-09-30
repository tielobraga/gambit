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

package ru.ming13.gambit.local.sqlite;


final class DbVersions
{
	private DbVersions() {
	}

	public static final int CURRENT = 3;
	public static final int LATEST_WITH_UPDATE_TIME_SUPPORT = 2;
	public static final int LATEST_WITH_CAMEL_NAMING_STYLE = 1;
}