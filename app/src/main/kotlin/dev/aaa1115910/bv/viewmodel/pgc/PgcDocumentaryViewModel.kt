package dev.aaa1115910.bv.viewmodel.pgc

import dev.aaa1115910.biliapi.repositories.PgcRepository
import dev.aaa1115910.biliapi.repositories.PgcType

class PgcDocumentaryViewModel(
    override val pgcRepository: PgcRepository
) : PgcViewModel(
    pgcRepository = pgcRepository,
    pgcType = PgcType.Documentary
)