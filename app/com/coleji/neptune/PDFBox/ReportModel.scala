package com.coleji.neptune.PDFBox

// Think of a report as a function from model to pdf output.
// Two reports instantiated with the same model should always produce exactly the same pdf
// ie a report is a pure function on a model
abstract class ReportModel
