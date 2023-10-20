package org.thinker.thinker.domain.dataretriever


sealed class DataRetrieverException : Exception()

/** @param permission is one of the String constants found in the Permission class
 * of the XXPermissions library.*/
class PermissionNotGranted(val permission: String) : DataRetrieverException()

class FileNotFound(val path: String) : DataRetrieverException()

class Unexpected : DataRetrieverException()