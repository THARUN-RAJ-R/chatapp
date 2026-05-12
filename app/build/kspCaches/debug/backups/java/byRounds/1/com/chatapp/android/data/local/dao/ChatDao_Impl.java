package com.chatapp.android.data.local.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.EntityUpsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.chatapp.android.data.local.entity.ChatEntity;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Long;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class ChatDao_Impl implements ChatDao {
  private final RoomDatabase __db;

  private final SharedSQLiteStatement __preparedStmtOfClearUnread;

  private final EntityUpsertionAdapter<ChatEntity> __upsertionAdapterOfChatEntity;

  public ChatDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__preparedStmtOfClearUnread = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE chats SET unreadCount = 0 WHERE id = ?";
        return _query;
      }
    };
    this.__upsertionAdapterOfChatEntity = new EntityUpsertionAdapter<ChatEntity>(new EntityInsertionAdapter<ChatEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT INTO `chats` (`id`,`type`,`name`,`avatarUrl`,`lastMessage`,`lastMessageTime`,`unreadCount`,`isGroup`) VALUES (?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final ChatEntity entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getType());
        if (entity.getName() == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, entity.getName());
        }
        if (entity.getAvatarUrl() == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.getAvatarUrl());
        }
        if (entity.getLastMessage() == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.getLastMessage());
        }
        if (entity.getLastMessageTime() == null) {
          statement.bindNull(6);
        } else {
          statement.bindLong(6, entity.getLastMessageTime());
        }
        statement.bindLong(7, entity.getUnreadCount());
        final int _tmp = entity.isGroup() ? 1 : 0;
        statement.bindLong(8, _tmp);
      }
    }, new EntityDeletionOrUpdateAdapter<ChatEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE `chats` SET `id` = ?,`type` = ?,`name` = ?,`avatarUrl` = ?,`lastMessage` = ?,`lastMessageTime` = ?,`unreadCount` = ?,`isGroup` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final ChatEntity entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getType());
        if (entity.getName() == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, entity.getName());
        }
        if (entity.getAvatarUrl() == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.getAvatarUrl());
        }
        if (entity.getLastMessage() == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.getLastMessage());
        }
        if (entity.getLastMessageTime() == null) {
          statement.bindNull(6);
        } else {
          statement.bindLong(6, entity.getLastMessageTime());
        }
        statement.bindLong(7, entity.getUnreadCount());
        final int _tmp = entity.isGroup() ? 1 : 0;
        statement.bindLong(8, _tmp);
        statement.bindString(9, entity.getId());
      }
    });
  }

  @Override
  public Object clearUnread(final String chatId, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfClearUnread.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, chatId);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfClearUnread.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object upsertAll(final List<ChatEntity> chats,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __upsertionAdapterOfChatEntity.upsert(chats);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object upsert(final ChatEntity chat, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __upsertionAdapterOfChatEntity.upsert(chat);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<ChatEntity>> getAllChats() {
    final String _sql = "SELECT * FROM chats ORDER BY lastMessageTime DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"chats"}, new Callable<List<ChatEntity>>() {
      @Override
      @NonNull
      public List<ChatEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfType = CursorUtil.getColumnIndexOrThrow(_cursor, "type");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfAvatarUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "avatarUrl");
          final int _cursorIndexOfLastMessage = CursorUtil.getColumnIndexOrThrow(_cursor, "lastMessage");
          final int _cursorIndexOfLastMessageTime = CursorUtil.getColumnIndexOrThrow(_cursor, "lastMessageTime");
          final int _cursorIndexOfUnreadCount = CursorUtil.getColumnIndexOrThrow(_cursor, "unreadCount");
          final int _cursorIndexOfIsGroup = CursorUtil.getColumnIndexOrThrow(_cursor, "isGroup");
          final List<ChatEntity> _result = new ArrayList<ChatEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ChatEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpType;
            _tmpType = _cursor.getString(_cursorIndexOfType);
            final String _tmpName;
            if (_cursor.isNull(_cursorIndexOfName)) {
              _tmpName = null;
            } else {
              _tmpName = _cursor.getString(_cursorIndexOfName);
            }
            final String _tmpAvatarUrl;
            if (_cursor.isNull(_cursorIndexOfAvatarUrl)) {
              _tmpAvatarUrl = null;
            } else {
              _tmpAvatarUrl = _cursor.getString(_cursorIndexOfAvatarUrl);
            }
            final String _tmpLastMessage;
            if (_cursor.isNull(_cursorIndexOfLastMessage)) {
              _tmpLastMessage = null;
            } else {
              _tmpLastMessage = _cursor.getString(_cursorIndexOfLastMessage);
            }
            final Long _tmpLastMessageTime;
            if (_cursor.isNull(_cursorIndexOfLastMessageTime)) {
              _tmpLastMessageTime = null;
            } else {
              _tmpLastMessageTime = _cursor.getLong(_cursorIndexOfLastMessageTime);
            }
            final int _tmpUnreadCount;
            _tmpUnreadCount = _cursor.getInt(_cursorIndexOfUnreadCount);
            final boolean _tmpIsGroup;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsGroup);
            _tmpIsGroup = _tmp != 0;
            _item = new ChatEntity(_tmpId,_tmpType,_tmpName,_tmpAvatarUrl,_tmpLastMessage,_tmpLastMessageTime,_tmpUnreadCount,_tmpIsGroup);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object getById(final String chatId, final Continuation<? super ChatEntity> $completion) {
    final String _sql = "SELECT * FROM chats WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, chatId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<ChatEntity>() {
      @Override
      @Nullable
      public ChatEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfType = CursorUtil.getColumnIndexOrThrow(_cursor, "type");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfAvatarUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "avatarUrl");
          final int _cursorIndexOfLastMessage = CursorUtil.getColumnIndexOrThrow(_cursor, "lastMessage");
          final int _cursorIndexOfLastMessageTime = CursorUtil.getColumnIndexOrThrow(_cursor, "lastMessageTime");
          final int _cursorIndexOfUnreadCount = CursorUtil.getColumnIndexOrThrow(_cursor, "unreadCount");
          final int _cursorIndexOfIsGroup = CursorUtil.getColumnIndexOrThrow(_cursor, "isGroup");
          final ChatEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpType;
            _tmpType = _cursor.getString(_cursorIndexOfType);
            final String _tmpName;
            if (_cursor.isNull(_cursorIndexOfName)) {
              _tmpName = null;
            } else {
              _tmpName = _cursor.getString(_cursorIndexOfName);
            }
            final String _tmpAvatarUrl;
            if (_cursor.isNull(_cursorIndexOfAvatarUrl)) {
              _tmpAvatarUrl = null;
            } else {
              _tmpAvatarUrl = _cursor.getString(_cursorIndexOfAvatarUrl);
            }
            final String _tmpLastMessage;
            if (_cursor.isNull(_cursorIndexOfLastMessage)) {
              _tmpLastMessage = null;
            } else {
              _tmpLastMessage = _cursor.getString(_cursorIndexOfLastMessage);
            }
            final Long _tmpLastMessageTime;
            if (_cursor.isNull(_cursorIndexOfLastMessageTime)) {
              _tmpLastMessageTime = null;
            } else {
              _tmpLastMessageTime = _cursor.getLong(_cursorIndexOfLastMessageTime);
            }
            final int _tmpUnreadCount;
            _tmpUnreadCount = _cursor.getInt(_cursorIndexOfUnreadCount);
            final boolean _tmpIsGroup;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsGroup);
            _tmpIsGroup = _tmp != 0;
            _result = new ChatEntity(_tmpId,_tmpType,_tmpName,_tmpAvatarUrl,_tmpLastMessage,_tmpLastMessageTime,_tmpUnreadCount,_tmpIsGroup);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
