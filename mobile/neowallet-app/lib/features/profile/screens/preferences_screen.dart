import 'package:flutter/material.dart';

import '../../../core/components/brand_logos.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';
import 'package:neowallet_app/core/di/providers.dart';

import '../../profile/models/user_preferences_model.dart';

class PreferencesScreen extends ConsumerStatefulWidget {
  const PreferencesScreen({super.key});

  @override
  ConsumerState<PreferencesScreen> createState() => _PreferencesScreenState();
}

class _PreferencesScreenState extends ConsumerState<PreferencesScreen> {
  final _formKey = GlobalKey<FormState>();
  final _localeController = TextEditingController();
  final _currencyController = TextEditingController();
  final _timezoneController = TextEditingController();
  final _aiLanguageController = TextEditingController();
  bool _budgetAlerts = true;
  bool _billReminders = true;
  bool _savingsUpdates = true;
  bool _financialHealthUpdates = true;
  String _aiResponseStyle = 'CONCISE';

  @override
  void initState() {
    super.initState();
    final prefs = ref.read(profileProvider).preferences;
    if (prefs != null) {
      _localeController.text = prefs.locale;
      _currencyController.text = prefs.currency;
      _timezoneController.text = prefs.timezone;
      _aiLanguageController.text = prefs.aiPreferences.language;
      _budgetAlerts = prefs.notificationPreferences.budgetAlerts;
      _billReminders = prefs.notificationPreferences.billReminders;
      _savingsUpdates = prefs.notificationPreferences.savingsUpdates;
      _financialHealthUpdates = prefs.notificationPreferences.financialHealthUpdates;
      _aiResponseStyle = prefs.aiPreferences.responseStyle;
    } else {
      ref.read(profileProvider.notifier).loadPreferences();
    }
  }

  @override
  void dispose() {
    _localeController.dispose();
    _currencyController.dispose();
    _timezoneController.dispose();
    _aiLanguageController.dispose();
    super.dispose();
  }

  String? _validateLocale(String? value) {
    if (value == null || !RegExp(r'^[a-zA-Z]{2}(-[a-zA-Z]{2})?$').hasMatch(value.trim())) {
      return 'Use format like en-US';
    }
    return null;
  }

  String? _validateCurrency(String? value) {
    if (value == null || !RegExp(r'^[A-Z]{3}$').hasMatch(value.trim())) {
      return 'Use 3-letter ISO code';
    }
    return null;
  }

  String? _validateTimezone(String? value) {
    if (value == null || value.trim().isEmpty || value.trim().length > 50) {
      return 'Invalid timezone';
    }
    return null;
  }

  Future<void> _save() async {
    if (!_formKey.currentState!.validate()) return;
    final prefs = UserPreferences(
      userId: ref.read(profileProvider).user?.userId ?? '',
      locale: _localeController.text.trim(),
      currency: _currencyController.text.trim().toUpperCase(),
      timezone: _timezoneController.text.trim(),
      notificationPreferences: NotificationPreferences(
        budgetAlerts: _budgetAlerts,
        billReminders: _billReminders,
        savingsUpdates: _savingsUpdates,
        financialHealthUpdates: _financialHealthUpdates,
      ),
      aiPreferences: AiPreferences(
        responseStyle: _aiResponseStyle,
        language: _aiLanguageController.text.trim(),
      ),
    );
    await ref.read(profileProvider.notifier).updatePreferences(prefs);
    if (mounted) {
      final error = ref.read(profileProvider).error;
      if (error == null) {
        context.pop();
      } else {
        ScaffoldMessenger.of(context).showSnackBar(SnackBar(content: Text(error)));
      }
    }
  }

  @override
  Widget build(BuildContext context) {
    final isLoading = ref.watch(profileProvider).isLoading;

    return Scaffold(
      appBar: const BrandAppBar(title: Text('Preferences')),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(16),
        child: Form(
          key: _formKey,
          child: Column(
            children: [
              TextFormField(
                controller: _localeController,
                decoration: const InputDecoration(labelText: 'Locale'),
                validator: _validateLocale,
              ),
              const SizedBox(height: 12),
              TextFormField(
                controller: _currencyController,
                decoration: const InputDecoration(labelText: 'Currency'),
                validator: _validateCurrency,
              ),
              const SizedBox(height: 12),
              TextFormField(
                controller: _timezoneController,
                decoration: const InputDecoration(labelText: 'Timezone'),
                validator: _validateTimezone,
              ),
              const Divider(height: 32),
              SwitchListTile(
                title: const Text('Budget Alerts'),
                value: _budgetAlerts,
                onChanged: (v) => setState(() => _budgetAlerts = v),
              ),
              SwitchListTile(
                title: const Text('Bill Reminders'),
                value: _billReminders,
                onChanged: (v) => setState(() => _billReminders = v),
              ),
              SwitchListTile(
                title: const Text('Savings Updates'),
                value: _savingsUpdates,
                onChanged: (v) => setState(() => _savingsUpdates = v),
              ),
              SwitchListTile(
                title: const Text('Financial Health Updates'),
                value: _financialHealthUpdates,
                onChanged: (v) => setState(() => _financialHealthUpdates = v),
              ),
              const Divider(height: 32),
              DropdownButtonFormField<String>(
                value: _aiResponseStyle,
                decoration: const InputDecoration(labelText: 'AI Response Style'),
                items: const [
                  DropdownMenuItem(value: 'CONCISE', child: Text('Concise')),
                  DropdownMenuItem(value: 'DETAILED', child: Text('Detailed')),
                ],
                onChanged: (v) => setState(() => _aiResponseStyle = v ?? 'CONCISE'),
              ),
              const SizedBox(height: 12),
              TextFormField(
                controller: _aiLanguageController,
                decoration: const InputDecoration(labelText: 'AI Language'),
                validator: (v) => (v == null || v.trim().isEmpty || v.length > 10) ? 'Invalid' : null,
              ),
              const SizedBox(height: 24),
              isLoading
                  ? const CircularProgressIndicator()
                  : ElevatedButton(
                      onPressed: _save,
                      child: const Text('Save Preferences'),
                    ),
            ],
          ),
        ),
      ),
    );
  }
}
