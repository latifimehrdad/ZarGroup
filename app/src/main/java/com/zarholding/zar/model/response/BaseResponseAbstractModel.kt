package com.zarholding.zar.model.response

/**
 * Created by m-latifi on 11/9/2022.
 */

abstract class BaseResponseAbstractModel {
    abstract val hasError : Boolean
    abstract val message : String
}