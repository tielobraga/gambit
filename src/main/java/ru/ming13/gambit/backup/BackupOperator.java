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

package ru.ming13.gambit.backup;

import android.content.Context;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.drive.Contents;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveId;

import java.io.InputStream;
import java.io.OutputStream;

import ru.ming13.gambit.database.DatabaseOperator;

public final class BackupOperator
{
	private final Context context;
	private final GoogleApiClient driveApiClient;

	public static BackupOperator with(Context context, GoogleApiClient driveApiClient) {
		return new BackupOperator(context, driveApiClient);
	}

	private BackupOperator(Context context, GoogleApiClient driveApiClient) {
		this.context = context.getApplicationContext();
		this.driveApiClient = driveApiClient;
	}

	public void exportBackup(DriveId backupFileId) {
		DriveFile backupFile = Drive.DriveApi.getFile(driveApiClient, backupFileId);
		Contents backupFileContents = backupFile.openContents(driveApiClient, DriveFile.MODE_WRITE_ONLY, null).await().getContents();

		OutputStream backupFileStream = backupFileContents.getOutputStream();
		DatabaseOperator.with(context).writeDatabaseContents(backupFileStream);

		backupFile.commitAndCloseContents(driveApiClient, backupFileContents).await();
	}

	public void importBackup(DriveId backupFileId) {
		DriveFile backupFile = Drive.DriveApi.getFile(driveApiClient, backupFileId);
		Contents backupFileContents = backupFile.openContents(driveApiClient, DriveFile.MODE_READ_ONLY, null).await().getContents();

		InputStream backupFileStream = backupFileContents.getInputStream();
		DatabaseOperator.with(context).readDatabaseContents(backupFileStream);

		backupFile.discardContents(driveApiClient, backupFileContents).await();
	}
}