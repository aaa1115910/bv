package dev.aaa1115910.bv.viewmodel.pgc

import dev.aaa1115910.biliapi.repositories.PgcRepository
import dev.aaa1115910.biliapi.repositories.PgcType

class PgcGuoChuangViewModel(
    override val pgcRepository: PgcRepository
) : PgcViewModel(
    pgcRepository = pgcRepository,
    pgcType = PgcType.GuoChuang
)