@php
$tabs = ['/admin/dashboard' => 'Dashboard', '/admin' => 'Consolidate Trips', '/admin/review' => 'Approve / Reject', '/admin/journeys' => 'AI Trip Planner', '/admin/finalise' => 'Finalise Trips', '/admin/quotes' => 'Create RFQ', '/admin/bulk-trips' => 'Bulk Upload Trips', '/admin/sites' => 'Clinical Sites', '/admin/map' => 'Map', '/admin/group-assignments' => 'Staff Assignments'];
$oldQuals = old('qualifications', []);
$oldYears = old('years', []);
$oldDepts = old('departments', []);
@endphp
<x-shell :user="$user" :active="'/admin/group-assignments'" :tabs="$tabs">
    <div class="stat-tiles" style="margin-bottom:20px;">
        <div class="stat-tile total">
            <div class="kicker">Staff members</div>
            <div class="number">{{ $staffMembers->count() }}</div>
            <div class="cta">Registered on the platform</div>
        </div>
        <div class="stat-tile approved">
            <div class="kicker">Groups covered</div>
            <div class="number">{{ $sections->sum(fn ($s) => $s['assignments']->filter()->count()) }}</div>
            <div class="cta">Year / department / qualification</div>
        </div>
        <div class="stat-tile pending">
            <div class="kicker">Groups unassigned</div>
            <div class="number">{{ $sections->sum(fn ($s) => $s['assignments']->filter(fn ($a) => $a === null)->count()) }}</div>
            <div class="cta">Visible to all staff</div>
        </div>
    </div>

    <div class="card" style="max-width:760px;">
        <h2>Create a staff member</h2>
        @if ($errors->any())
            <div class="msg error">{{ $errors->first() }}</div>
        @endif
        <form method="POST" action="/admin/group-assignments">
            @csrf
            <div class="grid">
                <div class="field">
                    <label>Name &amp; surname</label>
                    <input name="name" placeholder="e.g. Thabo Mokoena" value="{{ old('name') }}" required>
                </div>
                <div class="field">
                    <label>Email</label>
                    <input name="email" type="email" placeholder="e.g. mokoent@cput.ac.za" value="{{ old('email') }}" required>
                </div>
            </div>
            <div class="field">
                <label>Responsible for qualification(s) <span class="muted">(optional, select all that apply)</span></label>
                <div class="chip-grid">
                    @foreach ($qualifications as $qual)
                        <label class="chip">
                            <input type="checkbox" name="qualifications[]" value="{{ $qual }}" @checked(in_array($qual, $oldQuals))>
                            {{ $qual }}
                        </label>
                    @endforeach
                </div>
            </div>
            <div class="field">
                <label>Responsible for year(s) <span class="muted">(optional, select all that apply)</span></label>
                <div class="chip-grid">
                    @foreach ($years as $year)
                        <label class="chip">
                            <input type="checkbox" name="years[]" value="{{ $year }}" @checked(in_array($year, $oldYears))>
                            {{ $year }}
                        </label>
                    @endforeach
                </div>
            </div>
            <div class="field">
                <label>Responsible for department(s) <span class="muted">(optional, select all that apply)</span></label>
                <div class="chip-grid">
                    @foreach ($departments as $dept)
                        <label class="chip">
                            <input type="checkbox" name="departments[]" value="{{ $dept }}" @checked(in_array($dept, $oldDepts))>
                            {{ $dept }}
                        </label>
                    @endforeach
                </div>
            </div>
            <button class="btn" type="submit">Create staff member</button>
            <p class="hint" style="margin-top:10px;">A temporary password is generated and shown after saving — the new staff member is asked to change it on first login. Responsibility can be adjusted any time via "Edit responsibility" in the staff list below.</p>
        </form>
    </div>

    <div class="card">
        <h2>Staff members <span class="badge-count">{{ $staffMembers->count() }}</span></h2>
        @if ($staffList->isEmpty())
            <div class="empty">No staff members yet. Use the form above to add the first one.</div>
        @else
            <table>
                <thead><tr><th>Name</th><th>Email</th><th>Responsible for</th><th>Status</th><th style="width:290px;">Actions</th></tr></thead>
                <tbody>
                    @foreach ($staffList as $entry)
                        @php
                            $selQuals = $entry['assignments']->where('type', 'qualification')->pluck('value')->all();
                            $selYears = $entry['assignments']->where('type', 'year')->pluck('value')->all();
                            $selDepts = $entry['assignments']->where('type', 'department')->pluck('value')->all();
                        @endphp
                        <tr @if (!$entry['staff']->active) style="opacity:.55;" @endif>
                            <td>{{ $entry['staff']->name }}</td>
                            <td class="muted">{{ $entry['staff']->email }}</td>
                            <td>
                                @if ($entry['assignments']->isEmpty())
                                    <span class="muted">Unassigned — sees unmatched requests only</span>
                                @else
                                    @foreach ($entry['assignments'] as $a)
                                        <span class="pill" title="{{ $a['label'] }}" style="margin:0 4px 4px 0;">{{ $a['value'] }}</span>
                                    @endforeach
                                @endif
                            </td>
                            <td>
                                @if ($entry['staff']->active)
                                    <span class="pill" style="background:#e6f4ea;color:#2e7d32;border:0;">Active</span>
                                @else
                                    <span class="pill" style="background:#fdecea;color:#c62828;border:0;">Deactivated</span>
                                @endif
                            </td>
                            <td>
                                <div style="display:flex;gap:6px;flex-wrap:wrap;">
                                    <details style="position:relative;">
                                        <summary class="btn small secondary" style="cursor:pointer;">Edit responsibility</summary>
                                        <form method="POST" action="/admin/staff/{{ $entry['staff']->id }}/update" class="responsibility-popover">
                                            @csrf
                                            <div class="field">
                                                <label>Qualifications</label>
                                                <div class="chip-grid">
                                                    @foreach ($qualifications as $qual)
                                                        <label class="chip">
                                                            <input type="checkbox" name="qualifications[]" value="{{ $qual }}" @checked(in_array($qual, $selQuals))>
                                                            {{ $qual }}
                                                        </label>
                                                    @endforeach
                                                </div>
                                            </div>
                                            <div class="field">
                                                <label>Years</label>
                                                <div class="chip-grid">
                                                    @foreach ($years as $year)
                                                        <label class="chip">
                                                            <input type="checkbox" name="years[]" value="{{ $year }}" @checked(in_array($year, $selYears))>
                                                            {{ $year }}
                                                        </label>
                                                    @endforeach
                                                </div>
                                            </div>
                                            <div class="field">
                                                <label>Departments</label>
                                                <div class="chip-grid">
                                                    @foreach ($departments as $dept)
                                                        <label class="chip">
                                                            <input type="checkbox" name="departments[]" value="{{ $dept }}" @checked(in_array($dept, $selDepts))>
                                                            {{ $dept }}
                                                        </label>
                                                    @endforeach
                                                </div>
                                            </div>
                                            <button class="btn small" type="submit">Save responsibility</button>
                                        </form>
                                    </details>
                                    <form method="POST" action="/admin/staff/{{ $entry['staff']->id }}/reset-password">
                                        @csrf
                                        <button class="btn small" type="submit">Reset password</button>
                                    </form>
                                    <form method="POST" action="/admin/staff/{{ $entry['staff']->id }}/toggle">
                                        @csrf
                                        @if ($entry['staff']->active)
                                            <button class="btn small danger" type="submit">Deactivate</button>
                                        @else
                                            <button class="btn small" type="submit">Reactivate</button>
                                        @endif
                                    </form>
                                    <form method="POST" action="/admin/staff/{{ $entry['staff']->id }}" onsubmit="return confirm('Permanently delete {{ $entry['staff']->name }} and remove all their group assignments?');">
                                        @csrf
                                        @method('DELETE')
                                        <button class="btn small danger" type="submit">Delete</button>
                                    </form>
                                </div>
                            </td>
                        </tr>
                    @endforeach
                </tbody>
            </table>
        @endif
    </div>

    @foreach ($sections as $section)
        <div class="card">
            <h2>{{ $section['label'] }} staff assignments</h2>
            @if ($loop->first)
                <p class="hint">
                    Department and qualification groups are <strong>static</strong> — coverage is set per staff member via "Edit responsibility" in the staff list above. Year groups can be assigned either way. A staff member sees trip requests matching one of their assignments when reviewing/approving (plus any requests missing that field, since those can't be attributed). Admins always see every request.
                </p>
            @endif
            <table>
                <thead><tr><th>{{ $section['label'] }}</th><th>Responsible staff member</th>@if ($section['interactive'])<th></th>@endif</tr></thead>
                <tbody>
                    @forelse ($section['assignments'] as $value => $assignment)
                        <tr>
                            <td>{{ $value }}</td>
                            <td class="muted">{{ $assignment?->staff?->name ?? 'Unassigned' }}</td>
                            @if ($section['interactive'])
                                <td>
                                    <form method="POST" action="/admin/group-assignments/{{ $section['type'] }}/{{ rawurlencode($value) }}" style="display:flex;gap:8px;align-items:center;">
                                        @csrf
                                        <select name="staff_id">
                                            <option value="">— Unassigned —</option>
                                            @foreach ($staffMembers as $staff)
                                                <option value="{{ $staff->id }}" @selected($assignment?->staff_id === $staff->id)>{{ $staff->name }} ({{ $staff->email }})</option>
                                            @endforeach
                                        </select>
                                        <button class="btn small" type="submit">Save</button>
                                    </form>
                                </td>
                            @endif
                        </tr>
                    @empty
                        <tr><td colspan="3" class="muted">No options defined.</td></tr>
                    @endforelse
                </tbody>
            </table>
        </div>
    @endforeach
</x-shell>