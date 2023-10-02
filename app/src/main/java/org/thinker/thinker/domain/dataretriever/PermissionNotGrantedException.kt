package org.thinker.thinker.domain.dataretriever


/** @param permission is one of the String constants found in the Permission class
 * of the XXPermissions library.*/
class PermissionNotGrantedException(val permission: String) : Exception()